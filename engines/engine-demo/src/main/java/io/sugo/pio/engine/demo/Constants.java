package io.sugo.pio.engine.demo;

/**
 */
public class Constants {
    public static final String DATA_PATH = Constants.class.getClassLoader().getResource("movielen100k/u.data").getPath();
    public static final String USER_PATH = Constants.class.getClassLoader().getResource("movielen100k/u.user").getPath();
    public static final String ITEM_PATH = Constants.class.getClassLoader().getResource("movielen100k/u.item").getPath();

    public static final String DATA_SEPERATOR = "\t";

    public static final String ITEM_GENS = "unknown|Action|Adventure|Animation|Children's|Comedy|Crime|Documentary|Drama|Fantasy|Film-Noir|Horror|Musical|Mystery|Romance|Sci-Fi|Thriller|War|Western|";
    public static final String ITEM_SEPERATOR = "\\|";
}
