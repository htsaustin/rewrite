package com.netflix.java.refactor.ast

import com.netflix.java.refactor.parse.Parser
import org.junit.Assert.assertEquals
import org.junit.Test

abstract class LiteralTest(p: Parser): Parser by p {
    
    @Test
    fun literalField() {
        val a = parse("""
            public class A {
                int n = 0;
            }
        """)

        val literal = a.fields()[0].vars[0].initializer as Tr.Literal
        assertEquals(0, literal.value)
        assertEquals(Type.Tag.Int, literal.typeTag)
        assertEquals("0", literal.printTrimmed())
    }

    @Test
    fun literalCharacter() {
        val a = parse("""
            public class A {
                char c = 'a';
            }
        """)

        val literal = a.fields()[0].vars[0].initializer as Tr.Literal
        assertEquals('a', literal.value)
        assertEquals(Type.Tag.Char, literal.typeTag)
        assertEquals("'a'", literal.printTrimmed())
    }

    @Test
    fun literalNumerics() {
        val a = parse("""
            public class A {
                double d1 = 1.0d;
                double d2 = 1.0;
                long l1 = 1L;
                long l2 = 1;
            }
        """)

        val (d1, d2, l1, l2) = a.fields(0..3).map { it.vars[0].initializer as Tr.Literal }
        assertEquals("1.0d", d1.printTrimmed())
        assertEquals("1.0", d2.printTrimmed())
        assertEquals("1L", l1.printTrimmed())
        assertEquals("1", l2.printTrimmed())
    }

    @Test
    fun transformString() {
        val a = parse("""
            public class A {
                String s = "foo ''";
            }
        """)

        val literal = a.fields()[0].vars[0].initializer as Tr.Literal
        assertEquals("\"foo\"", literal.transformValue<String> { it.substringBefore(' ') })
    }

    @Test
    fun nullLiteral() {
        val a = parse("""
            public class A {
                String s = null;
            }
        """)

        assertEquals("null", a.fields()[0].vars[0].initializer?.printTrimmed())
    }

    @Test
    fun transformLong() {
        val a = parse("""
            public class A {
                Long l = 2L;
            }
        """)

        val literal = a.fields()[0].vars[0].initializer as Tr.Literal
        assertEquals("4L", literal.transformValue<Long> { it * 2 })
    }

    @Test
    fun variationInSuffixCasing() {
        val a = parse("""
            public class A {
                Long l = 0l;
                Long m = 0L;
            }
        """)

        val (lower, upper) = a.fields(0..1).map { it.vars[0].initializer as Tr.Literal }

        assertEquals("0L", upper.printTrimmed())
        assertEquals("0l", lower.printTrimmed())
    }

    @Test
    fun escapedString() {
        val a = parse("""
            public class A {
                String s = "\"";
            }
        """)

        val s = a.fields()[0].vars[0].initializer as Tr.Literal
        assertEquals("\"\\\"\"", s.printTrimmed())
    }

    @Test
    fun escapedCharacter() {
        val a = parse("""
            public class A {
                char c = '\'';
            }
        """)

        val s = a.fields()[0].vars[0].initializer as Tr.Literal
        assertEquals("'\\''", s.printTrimmed())
    }
}