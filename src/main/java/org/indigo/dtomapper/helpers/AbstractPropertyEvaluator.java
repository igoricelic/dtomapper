package org.indigo.dtomapper.helpers;

import org.indigo.dtomapper.exceptions.IllegalStateException;
import org.indigo.dtomapper.helpers.specification.ReflectionHelper;
import org.indigo.dtomapper.metadata.enums.Direction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

abstract class AbstractPropertyEvaluator {

    private static final String ROOT_TOKEN = "#";
    private static final String PATH_SPLIT_REGEX = "\\.";

    /*
     * valid matches:
     *  filed
     *  field.field
     *  #.field.field
     *  #field.field.field
     */
    private static final String REGEX = "((\\w+|#\\w+|#)(?:\\.\\w+)*)";

    private final Pattern pattern;

    final ReflectionHelper reflectionHelper;

    AbstractPropertyEvaluator(ReflectionHelper reflectionHelper) {
        this.pattern = Pattern.compile(REGEX);
        this.reflectionHelper = reflectionHelper;
    }

    boolean isValidPath(String path) {
        Assert.checkNotNull(path);
        return pattern.matcher(path).matches();
    }

    List<String> parse(String path) {
        if(!isValidPath(path))
            throw new IllegalStateException(String.format("Path '%s' isn't valid!", path));
        List<String> tokens = new ArrayList<>(Arrays.asList(path.split(PATH_SPLIT_REGEX)));
        String firstToken = tokens.get(0);
        if(ROOT_TOKEN.equals(firstToken)) {
            if(tokens.size() > 1) tokens.remove(0);
        } else if(firstToken.startsWith(ROOT_TOKEN)) {
            // when first token starts with #, for example #user.address
            tokens.remove(0);
            tokens.add(0, firstToken.substring(1));
        }
        return tokens;
    }

    boolean isRootProperty(List<String> path) {
        return path.size() == 1 && ROOT_TOKEN.equals(path.get(0));
    }

    boolean isIgnorableDirection(Direction direction) {
        return Direction.Bidirectional.equals(direction) || Direction.Outgoing.equals(direction);
    }

}
