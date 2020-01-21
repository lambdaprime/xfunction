package id.xfunction.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

import id.xfunction.SmartArgs;

public class SmartArgsTest {

    private StringJoiner buf;
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
        buf = new StringJoiner("\n");

        SmartArgs args = new SmartArgs(handlers, defaultHandler);
        args.parse(new String[] {"-k" , "value"});
        assertEquals("Dir: value", buf.toString());
        
        buf = new StringJoiner("\n");
        args.parse(new String[] {"-k" , "value", "-v"});
        assertEquals("Dir: value\nVerbose", buf.toString());

        buf = new StringJoiner("\n");
        args.parse(new String[] {"-k" , "value", "-v", "-e", "expr"});
        assertEquals("Dir: value\nVerbose\nExpression: expr", buf.toString());

        buf = new StringJoiner("\n");
        args.parse(new String[] {"-k" , "value", "-v", "-e", "expr", "--no-wait"});
        assertEquals("Dir: value\nVerbose\nExpression: expr\nNo wait", buf.toString());
    }

    @Test
    public void test_unknown_arg() throws Exception {
        SmartArgs args = new SmartArgs(handlers, defaultHandler);
        assertThrows(RuntimeException.class, () -> args.parse(new String[] {"-g", "ggg"}));
    }
}
