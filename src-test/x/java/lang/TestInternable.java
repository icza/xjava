/*
 * Project XJava
 * 
 * Copyright (c) 2014 Andras Belicza <iczaaa@gmail.com>
 * 
 * This software is the property of Andras Belicza.
 * Copying, modifying, distributing, refactoring without the author's permission
 * is prohibited and protected by Law.
 */
package x.java.lang;

/**
 * Test implementation of {@link BaseInternable}.
 * 
 * @author Andras Belicza
 */
class TestInternable extends BaseInternable<TestInternable> {
    
    /** Some "custom" data. */
    private final int value;
    
    /** Counter of {@link #customHashCode()} method calls. */
    public int customHashCodeCount;
    /** Counter of {@link #customEquals(TestInternable)} method calls. */
    public int customEqualsCount;
    
    /**
     * Creates a new {@link TestInternable}.
     * 
     * @param value value of the custom data
     */
    public TestInternable(final int value) {
        this.value = value;
    }
    
    @Override
    protected int customHashCode() {
        customHashCodeCount++;
        return value;
    }
    
    @Override
    protected boolean customEquals(TestInternable obj) {
        customEqualsCount++;
        return value == obj.value;
    }
    
}
