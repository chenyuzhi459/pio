package io.sugo.pio.ffm;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.example.table.DataRow;
import io.sugo.pio.example.table.DataRowReader;
import io.sugo.pio.example.table.ExampleTable;
import io.sugo.pio.example.table.NominalMapping;
import io.sugo.pio.tools.Ontology;

import java.io.*;
import java.util.*;

/**
 * @author chenhuang
 */
public class FFMProblem {

//    private static final Logger logger = new Logger(FFMProblem.class);

    //data : field_num:feature_num:value
    // max(feature_num) + 1
    public int n;
    // max(field_num) + 1
    public int m;
    // data set size(number of rows)
    public int l;
    // X[ [P[0], P[1]) ], length=nnz
    public FFMNode[] X;
    // length=l+1
    public int[] P;
    // Y[0], length=l
    public float[] Y;

    public static FFMProblem readFFMProblem(String path) throws IOException {
        FFMProblem problem = new FFMProblem();
        int l = 0, nnz = 0;
        BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(new File(path)), "UTF-8"));
        String line = null;
        while ((line = br.readLine()) != null) {
            l += 1;
            String[] fields = line.split(" |\t");
            for (int i = 1; i < fields.length; i++) {
                nnz += 1;
            }
        }
        br.close();

        System.out.printf("reading %s, instance_num: %d, nnz: %d\n", path, l, nnz);

        problem.l = l;
        problem.X = new FFMNode[nnz];
        problem.Y = new float[l];
        problem.P = new int[l + 1];
        problem.P[0] = 0;

        br = new BufferedReader(new InputStreamReader(
                new FileInputStream(new File(path)), "UTF-8"));
        int p = 0;
        for (int i = 0; (line = br.readLine()) != null; i++) {
            String[] fields = line.split(" |\t");
            problem.Y[i] = (Integer.parseInt(fields[0]) > 0) ? 1.f : -1.f;
            for (int j = 1; j < fields.length; j++) {
                String[] subFields = fields[j].split(":");
                FFMNode node = new FFMNode();
                node.f = Integer.parseInt(subFields[0]); // field_id
                node.j = Integer.parseInt(subFields[1]); // feature_id
                node.v = Float.parseFloat(subFields[2]); // value
                problem.X[p] = node;
                problem.m = Math.max(problem.m, node.f + 1);
                problem.n = Math.max(problem.n, node.j + 1);
                p++;
            }
            problem.P[i + 1] = p;
        }
        br.close();

        return problem;
    }

    public static FFMProblem convertExampleSet(ExampleSet exampleSet) {
        List<String> ffmList = new ArrayList<>();
        Map<String, Integer> fieldMap = new HashMap<>();
        Map<String, Integer> featureMap = new HashMap<>();

        ExampleTable exampleTable = exampleSet.getExampleTable();
        Attribute[] attributes = exampleTable.getAttributes();
        Attribute labelAttr = exampleSet.getAttributes().getLabel();

        int fieldIndex = 0, featureIndex = 0;
        for (int i = 0; i < attributes.length; i++) {
            Attribute attribute = attributes[i];
            String attributeName = attribute.getName();
            int valueType = attribute.getValueType();
            // label attribute not participate in calculate
            if (attribute.equals(labelAttr)) {
                continue;
            }

            if (Ontology.ATTRIBUTE_VALUE_TYPE.isA(valueType, Ontology.NOMINAL)) {
                NominalMapping nominalMapping = attribute.getMapping();
                List<String> nominalValues = nominalMapping.getValues();
                for (String nominalValue : nominalValues) {
                    int mappedValue = nominalMapping.getIndex(nominalValue);
                    featureMap.put(attributeName + "_" + mappedValue, featureIndex++);
                }
            } else {
                featureMap.put(attributeName, featureIndex++);
            }

            fieldMap.put(attributeName, fieldIndex++);
        }

        FFMProblem problem = new FFMProblem();
        int datasetSize = exampleSet.size();
        int attributeSize = attributes.length;
        problem.l = datasetSize;
        problem.X = new FFMNode[datasetSize * (attributeSize - 1)]; // exclude label attribute
        problem.Y = new float[datasetSize];
        problem.P = new int[datasetSize + 1];
        problem.P[0] = 0;

        int rows = 0, p = 0;
        DataRowReader dataRowReader = exampleTable.getDataRowReader();
        while (dataRowReader.hasNext()) {
            DataRow dataRow = dataRowReader.next();

            String ffmStr = "";
            for (int i = 0; i < attributes.length; i++) {
                Attribute attribute = attributes[i];
                int valueType = attribute.getValueType();
                String attributeName = attribute.getName();
                double mappedValue = dataRow.get(attribute);
                int mappedIntValue = (int) mappedValue;

                if (attribute.equals(labelAttr)) {
                    problem.Y[rows] = (mappedValue > 0) ? 1.f : -1.f;
                    continue;
                }

                FFMNode node = new FFMNode();

                if (Ontology.ATTRIBUTE_VALUE_TYPE.isA(valueType, Ontology.NOMINAL)) {
                    ffmStr += fieldMap.get(attributeName).toString() + ":" +
                            featureMap.get(attributeName + "_" + mappedIntValue) + ":1 ";

                    node.f = fieldMap.get(attributeName); // field_id
                    node.j = featureMap.get(attributeName + "_" + mappedIntValue); // feature_id
                    node.v = 1; // value
                } else {
                    ffmStr += fieldMap.get(attributeName).toString() + ":" +
                            featureMap.get(attributeName) + ":" + mappedValue + " ";

                    node.f = fieldMap.get(attributeName); // field_id
                    node.j = featureMap.get(attributeName); // feature_id
                    node.v = (float) mappedValue; // value
                }

                problem.X[p] = node;
                problem.m = Math.max(problem.m, node.f + 1);
                problem.n = Math.max(problem.n, node.j + 1);
                p++;
            }
            problem.P[rows + 1] = p;
            rows++;
            ffmList.add(ffmStr);
        }

        /*if (logger.isDebugEnabled()) {
            ffmList.forEach(ffmStr -> {
                logger.debug(ffmStr);
            });
        }*/

        System.out.printf("Converted result of FFMProblem: " + problem);

        return problem;
    }

    @Override
    public String toString() {
        return "FFMProblem [n=" + n + ", m=" + m + ", l=" + l + ", X="
                + Arrays.toString(X) + ", P=" + Arrays.toString(P) + ", Y="
                + Arrays.toString(Y) + "]";
    }

    public static void main(String[] args) throws IOException {
        FFMProblem problem = FFMProblem.readFFMProblem("aaa");
        System.out.println(problem);
    }

}
