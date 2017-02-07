package io.sugo.pio.guice;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.collect.ImmutableList;
import com.google.inject.Binder;
import io.sugo.pio.initialization.PioModule;
import io.sugo.pio.operator.extension.jdbc.io.DatabaseDataReader;
import io.sugo.pio.operator.learner.functions.LogisticRegression;
import io.sugo.pio.operator.learner.tree.ParallelDecisionTreeLearner;
import io.sugo.pio.operator.preprocessing.filter.ChangeAttributeRole;
import io.sugo.pio.operator.preprocessing.filter.ExampleFilter;
import io.sugo.pio.operator.preprocessing.filter.attributes.AttributeFilter;

import java.util.List;

/**
 */
public class ProcessingPioModule implements PioModule {
    @Override
    public List<? extends Module> getJacksonModules() {
        return ImmutableList.of(
                new SimpleModule(ProcessingPioModule.class.getSimpleName())
                        .registerSubtypes(
//                                new NamedType(SQLExecution.class, "sql_execution"),
                                new NamedType(DatabaseDataReader.class, "db_data_reader"),
                                new NamedType(AttributeFilter.class, "attribute_filter"),
                                new NamedType(ExampleFilter.class, "example_filter"),
                                new NamedType(ChangeAttributeRole.class, "change_attribute_role"),
                                new NamedType(ParallelDecisionTreeLearner.class, "decision_tree_learner"),
                                new NamedType(LogisticRegression.class, "logistic_regression")
                        )
        );
    }

    @Override
    public void configure(Binder binder) {
    }
}
