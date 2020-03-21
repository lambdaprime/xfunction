package id.xfunction;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import id.xfunction.TemplateMatcher;

public class TemplateMatcherTests {

    @Test
    public void test_empty_template() {
        TemplateMatcher matcher = new TemplateMatcher("");
        assertTrue(matcher.matches(""));
        assertFalse(matcher.matches("ffff"));
    }

    @Test
    public void test_empty_string() {
        TemplateMatcher matcher = new TemplateMatcher("lol");
        assertFalse(matcher.matches(""));
        assertFalse(matcher.matches("ffff"));
    }

    @Test
    public void test_matches_no_wildcard() {
        TemplateMatcher matcher = new TemplateMatcher("lol");
        assertTrue(matcher.matches("lol"));
    }

    @Test
    public void test_matches_wildcard() {
        TemplateMatcher matcher = new TemplateMatcher("lol*lol");
        assertTrue(matcher.matches("lolasdlol"));
        assertTrue(matcher.matches("lolasdloalol"));
        assertTrue(matcher.matches("lollol"));
        assertTrue(matcher.matches("lolXlol"));
        assertTrue(matcher.matches("lollollol"));
        assertFalse(matcher.matches("looooollol"));
        assertFalse(matcher.matches("looooollol"));
        assertFalse(matcher.matches("looooolololol"));
        assertFalse(matcher.matches("looooololollol"));
        assertTrue(matcher.matches("lolooololollol"));
    }

    @Test
    public void test_matches_wildcards() {
        TemplateMatcher matcher = new TemplateMatcher("abc*fun*kot");
        assertTrue(matcher.matches("abcccccfunkot"));
        assertTrue(matcher.matches("abcccccfunnnkot"));
        assertTrue(matcher.matches("abcccccfunfunkot"));
        assertTrue(matcher.matches("abcccccfununkolfunnnkot"));
        assertTrue(matcher.matches("abcccccfununkolfunnnkot"));
    }

    @Test
    public void test_matches_everything() {
        TemplateMatcher matcher = new TemplateMatcher("*");
        assertTrue(matcher.matches("abcccccfunkot"));
    }

    @Test
    public void test_matches_wildcard_start() {
        TemplateMatcher matcher = new TemplateMatcher("*fun");
        assertTrue(matcher.matches("abcccccfun"));
        
        matcher = new TemplateMatcher("*fun*");
        assertTrue(matcher.matches("abcccccfun"));
        assertTrue(matcher.matches("abcccccfunnnn"));
    }

}
