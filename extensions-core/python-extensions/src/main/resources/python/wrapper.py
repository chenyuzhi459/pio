# coding=utf-8

import sys
from glob import glob
import json
import os
import traceback

try:
    import cPickle as pickle
except:
    import pickle
try:
    import pandas
except:
    print("pandas module not found")
    sys.exit(50)

#writes string to error file    
def writeToErrorLog(full_message):
    if sys.version_info >= (3, 0):
        with open("rapidminer_error.log", "wt", encoding='utf-8') as error_file:
            error_file.write(full_message)
    else:
         with open("rapidminer_error.log", "w") as error_file:
            error_file.write(unicode(full_message,'utf-8'))


#check if pandas version is at least 0.12.0    
pandas_major_version = pandas.__version__.split('.')[1]
if float(pandas_major_version) < 12:
    print("pandas version "+pandas.__version__+" not supported")
    writeToErrorLog(pandas.__version__)
    sys.exit(51)


#prints the exception and writes it to the error file
#"userscript.py" is replaced by "script"             
def handleException(e):
    message = str(e).replace("userscript.py","script")
    lineinfo = ''
    if type(e) is SyntaxError:
        # wrong syntax with caret is contained in middle lines
        print(''.join(traceback.format_exception_only(type(e), e)[1:-1]))
        # line is already contained in message and must be shifted by one
        message = message.replace("line "+str(e.lineno), "line "+str(e.lineno-1))
    else:
        # extract function name, line number and line content
        info = traceback.extract_tb(sys.exc_info()[2])
        # find portion that starts with rm_main
        script_filename = ''
        found = False
        last_script_line = -1
        sanitized_info = []
        # sanitize traceback information
        for (filename,line_number,function,text) in info:
            if function == 'rm_main':
                script_filename = filename
                found = True
            if found:
                if filename == script_filename:
                    # change name of temp filename
                    filename = 'script'
                    # line number must be shifted by 1 
                    line_number -=1
                    last_script_line = line_number
                sanitized_info.append((filename,line_number,function,text))
        # print the sanitized traceback
        print("Traceback (most recent call last):")
        print(''.join(traceback.format_list(sanitized_info)))
        if last_script_line >=0:
            lineinfo = " (script, line {})".format(last_script_line)
    full_message = "{}: {}".format(type(e).__name__,message+lineinfo)
    print(full_message)
    writeToErrorLog(full_message)


try:
    from userscript import rm_main
except Exception as e: 
    print("failed to parse the script")
    handleException(e)
    sys.exit(55)


# the number of outputs which the operator expects    
rapidminer_numberOfOutputs = int(float(sys.argv[1]))

# checks for files of the form 'rapidminer_input*.*', 
# reads those with extension .csv into pandas.DataFrames,
# reads those with extension .bin into python objects     
# reads those with extension .foi into file objects
def deserialize():
    files = glob('rapidminer_input*.*');
    files.sort()
    inputs = []
    for file in files:
        extension = os.path.splitext(file)[1]
        if(extension=='.csv'):
            inputs.append(readExampleSet(file))
        elif(extension=='.bin'):
            try:
                with open(file,'rb') as load_file:
                    inputs.append(pickle.load(load_file))
            except Exception as e:
                handleException(e)
                sys.exit(65)            
        elif(extension=='.foi'):
            with open(file,'rb') as f:
                content = f.read().decode("utf-8")
            if os.path.exists(content):
                load_file=open(content,'r')
                inputs.append(load_file)
            else:
                print("File not found. The file {} does not exist.").format(content)
                with open("rapidminer_error.log", "w") as error_file:
                    error_file.write("The file {} does not exist.".format(content))
                sys.exit(70)
    return inputs


# reads the example set from the file into a pandas.DataFrame.
# checks if there is a metadata file and if this is the case
# attaches the metadata as pandas.DataFrame to the field metadata    
def readExampleSet(file):
    #check for same file name with meta data extension
    mdfile = os.path.splitext(file)[0]+'.pmd'    
    try:
        if sys.version_info >= (3, 0):
            with open(mdfile,'rt',encoding='utf-8') as data_file:    
                metadata = json.load(data_file,encoding = 'utf-8')  
        else:
            with open(mdfile) as data_file:    
                metadata = json.load(data_file,encoding = 'utf-8') 
        date_set = set(['date','time','date_time'])
        date_columns = []
        meta_dict={}       
        #different iteration methods for python 2 and 3
        try:
            items = metadata.iteritems()
        except AttributeError:
            items = metadata.items() 
        for key, value in items:
             #convert to tuple
            meta_dict[key]=(value[0],None if value[1]=="attribute" else value[1])
            #store date columns for parsing
            if value[0] in date_set:
                date_columns.append(key)
        #read example set from csv
        try:
            data = pandas.read_csv(file,index_col=None,encoding='utf-8',parse_dates=date_columns,infer_datetime_format=True)
        except TypeError: 
        #if the argument inter_datetime_format is not allowed in the current version do without
            data = pandas.read_csv(file,index_col=None,encoding='utf-8',parse_dates=date_columns)
        data.rm_metadata = meta_dict
    except:
        #no metadata found or reading with meta data failed
        print("failed to use the meta data")
        data = pandas.read_csv(file,index_col=None,encoding='utf-8')
        data.rm_metadata = None    
    return data


# writes the result(s) into files of the form rapidminer_output*.*,
# if the result is a tuple, all entries are treated separately
# exports pandas.DataFrames to csv-files, files to foi-files (containing the file os.path) and other objects except for tuples
# are serialized and saved in a .bin-file
def serialize(result):
    if not type(result) is tuple:
        result = (result,)
    index = 0
    for entry in result:
        if index == rapidminer_numberOfOutputs:
            break
        if isinstance(entry, pandas.DataFrame):
            handleMetaData(entry,index)
            checkColumnNames(entry)
            entry.to_csv("rapidminer_output%03d.csv" % index,index=False,encoding='utf-8')
        elif isFileObject(entry):
            # write path in foi-file
            with open("rapidminer_output%03d.foi" % index, 'wb') as foi_file:
                if sys.version_info >= (3, 0):
                    foi_file.write(bytes(entry.name, 'UTF-8'))
                else:
                    foi_file.write(entry.name)
        else:
            try:
                with open("rapidminer_output%03d.bin" % index, 'wb') as dump_file:
                    pickle.dump(entry,dump_file)
            except Exception as e:
                handleException(e)
                sys.exit(66) 
        index +=1

#writes the meta data to a file
#uses the meta data from rm_metadata attribute if present
#otherwise deduces the type from the data and sets no special role
def handleMetaData(data,index):
    metadata_list = []
    
    #check if rm_metadata attribute is present and a dictionary
    try:
        if isinstance(data.rm_metadata,dict):
            meta_isdict=True
        else:
            meta_isdict = False
            if data.rm_metadata is not None:
                print("Warning: rm_metadata must be a dictionary")
    except:
        meta_isdict=False
    
    for name in data.columns.values:
        try:
            meta = data.rm_metadata[name]
            #take entries only if tuple of length 2
            if isinstance(meta,tuple) and len(meta)==2 and meta_isdict:
                meta_type, meta_role = meta
            else:
                if meta_isdict and meta is not None:
                    print("Warning: rm_metadata["+name+"] must be a tuple of length 2, e.g. data.rm_metadata['column1']=('binominal','label')")
                meta_type = None
                meta_role= None
        except:
            meta_type = None
            meta_role= None
        
        if meta_role is None:
            meta_role = 'attribute'
        #choose type by dtype of the column
        if meta_type is None:
            kind_char = data.dtypes[name].kind
            if kind_char in ('i','u'):
                meta_type = 'integer'
            elif kind_char in ('f'):
                meta_type = 'real'
            elif kind_char in ('M'):
                meta_type = 'date_time'
            elif kind_char in ('b'):
                meta_type = 'binominal'
            else:
                meta_type = 'polynomial'
        metadata_list.append((meta_type,meta_role))
    #store as json
    try:
        with open("rapidminer_output%03d.pmd" % index, 'w') as dump_file:
            json.dump(metadata_list,dump_file)
    except Exception as e:
        print("Failed to send meta data from Python script to RapidMiner")


# if value has a string representation containing non-ascii symbols in python 2, 
# for example if value is a python 2 unicode with umlauts, then str(value) results in exception;
# in this case it is in particular not empty and contains more than only digits 
def isstringable(value):
    try: 
        str(value)
        return True
    except:
        return False
 
def checkColumnNames(dataFrame):
    # column name must not be empty or a number
    if any(isstringable(value) and ((not str(value)) or str(value).isdigit()) for value in dataFrame.columns.values):
        new_columns = ['att'+str(value) if (isstringable(value) and ((not str(value)) or str(value).isdigit())) else str(value) for value in dataFrame.columns.values]
        dataFrame.columns = new_columns


    
def wrapper(func, *args):
    return func(*args)
    
def isFileObject(entry):
    try:
        if entry.closed:
            open(entry)
            close(entry)
        return True
    except Exception:
        return False

if __name__ == "__main__":
    try:
        inputs = deserialize()
        try:
            #execute the userscript
            result = wrapper(rm_main, *inputs)
        except Exception as e:
            handleException(e)
            sys.exit(60)
        if not result is None and rapidminer_numberOfOutputs > 0:
            serialize(result)
    except Exception as e:
        #unknown/non-user error
         handleException(e)
         sys.exit(1)
