package id.xfunction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

import id.xfunction.SmartArgs;

public class SmartArgsTest {

    private List<String> buf = new ArrayList<>();
    private Map<String, Consumer<String>> handlers = new HashMap<String, Consumer<String>>() {{
        put("-k", val -> buf.add("Dir: " + val));
        put("-e", expr -> buf.add("Expression: " + expr));
        put("-d", dir -> buf.add("Dir: " + dir));
    }};
    private Function<String, Boolean> defaultHandler = arg -> {
        switch (arg) {
        case "-v": buf.add("Verbose"); return true;
        case "--no-wait": buf.add("No wait"); return true;
        }
        throw new RuntimeException();
    };

    @Test
    public void test_run() throws Exception {
        SmartArgs args = new SmartArgs(handlers, defaultHandler);
        args.parse(new String[] {"-k" , "value"});
        assertEquals("[Dir: value]", buf.toString());
        
        buf.clear();
        args.parse(new String[] {"-k" , "value", "-v"});
        assertEquals("[Dir: value, Verbose]", buf.toString());

        buf.clear();
        args.parse(new String[] {"-k" , "value", "-v", "-e", "expr"});
        assertEquals("[Dir: value, Verbose, Expression: expr]", buf.toString());

        buf.clear();
        args.parse(new String[] {"-k" , "value", "-v", "-e", "expr", "--no-wait"});
        assertEquals("[Dir: value, Verbose, Expression: expr, No wait]", buf.toString());
    }

    @Test
    public void test_unknown_arg() throws Exception {
        SmartArgs args = new SmartArgs(handlers, defaultHandler);
        assertThrows(RuntimeException.class, () -> args.parse(new String[] {"-g", "ggg"}));
    }

    @Test
    public void test_positional_args() throws Exception {
        List<String> args = new ArrayList<>();
        new SmartArgs(handlers, arg -> args.add(arg)).parse(new String[] {"-k" , "value", "arg1", "arg2", "arg3"});
        assertEquals("[Dir: value]", buf.toString());
        assertEquals("[arg1, arg2, arg3]", args.toString());
    }
}
