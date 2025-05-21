package com.coladungeon.items;

import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

/**
 * A dynamic category system that extends Generator's functionality.
 * This allows runtime registration of new item types.
 */
public class DynamicCategory {
    private String name;
    private float firstProb;
    private float secondProb;
    private Class<? extends Item> superClass;
    
    private List<Class<? extends Item>> classes = new ArrayList<>();
    private List<Float> probs = new ArrayList<>();
    private List<Float> defaultProbs = new ArrayList<>();
    private List<Float> defaultProbs2 = null;
    
    // These match Generator.Category variables for compatibility
    public boolean using2ndProbs = false;
    public Long seed = null;
    public int dropped = 0;
    public List<Float> defaultProbsTotal = null;
    
    public DynamicCategory(String name, float firstProb, float secondProb, Class<? extends Item> superClass) {
        this.name = name;
        this.firstProb = firstProb;
        this.secondProb = secondProb;
        this.superClass = superClass;
    }
    
    /**
     * Register a new item class with this category
     * @param itemClass The class to register
     * @param probability The probability weight for generation
     * @param probability2 Optional second deck probability (if using dual decks)
     * @return The index of the newly added item
     */
    public int registerItem(Class<? extends Item> itemClass, float probability, Float probability2) {
        classes.add(itemClass);
        defaultProbs.add(probability);
        probs.add(probability);
        
        if (probability2 != null) {
            if (defaultProbs2 == null) {
                defaultProbs2 = new ArrayList<>();
                // Fill previously added items with matching probabilities
                for (int i = 0; i < defaultProbs.size() - 1; i++) {
                    defaultProbs2.add(defaultProbs.get(i));
                }
            }
            defaultProbs2.add(probability2);
            
            // Update the total for combined decks
            if (defaultProbsTotal == null) {
                defaultProbsTotal = new ArrayList<>();
                // Fill previously added items
                for (int i = 0; i < defaultProbs.size() - 1; i++) {
                    defaultProbsTotal.add(defaultProbs.get(i) + defaultProbs2.get(i));
                }
            }
            defaultProbsTotal.add(probability + probability2);
        } else if (defaultProbs2 != null) {
            // Add matching probability if using secondary deck but none specified
            defaultProbs2.add(probability);
            defaultProbsTotal.add(probability * 2);
        }
        
        return classes.size() - 1;
    }
    
    /**
     * Register a new item class with this category (single deck only)
     */
    public int registerItem(Class<? extends Item> itemClass, float probability) {
        return registerItem(itemClass, probability, null);
    }
    
    /**
     * Check if this category has a secondary deck of probabilities
     */
    public boolean hasSecondaryDeck() {
        return defaultProbs2 != null && !defaultProbs2.isEmpty();
    }
    
    /**
     * Generate a random item from this category
     */
    public Item random() {
        if (probs.isEmpty()) return null;
        
        if (seed != null) {
            Random.pushGenerator(seed);
            for (int i = 0; i < dropped; i++) Random.Long();
        }
        
        // Get weighted random index
        float[] probsArray = new float[probs.size()];
        for (int i = 0; i < probs.size(); i++) {
            probsArray[i] = probs.get(i);
        }
        
        int index = Random.chances(probsArray);
        if (index == -1) {
            reset();
            for (int i = 0; i < probs.size(); i++) {
                probsArray[i] = probs.get(i);
            }
            index = Random.chances(probsArray);
        }
        
        // Reduce probability
        probs.set(index, probs.get(index) - 1);
        
        if (seed != null) {
            Random.popGenerator();
            dropped++;
        }
        
        // Create instance
        try {
            return ((Item) classes.get(index).newInstance()).random();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Reset probabilities to defaults
     */
    public void reset() {
        if (hasSecondaryDeck()) {
            using2ndProbs = !using2ndProbs;
            if (using2ndProbs) {
                for (int i = 0; i < defaultProbs2.size(); i++) {
                    probs.set(i, defaultProbs2.get(i));
                }
            } else {
                for (int i = 0; i < defaultProbs.size(); i++) {
                    probs.set(i, defaultProbs.get(i));
                }
            }
        } else {
            for (int i = 0; i < defaultProbs.size(); i++) {
                probs.set(i, defaultProbs.get(i));
            }
        }
    }
    
    // Getters
    public String getName() { return name; }
    public float getFirstProb() { return firstProb; }
    public float getSecondProb() { return secondProb; }
    public Class<? extends Item> getSuperClass() { return superClass; }
    public List<Class<? extends Item>> getClasses() { return classes; }
    public List<Float> getProbs() { return probs; }
    
    // Static storage for all dynamic categories
    private static HashMap<String, DynamicCategory> dynamicCategories = new HashMap<>();
    
    /**
     * Get or create a dynamic category
     */
    public static DynamicCategory get(String name, float firstProb, float secondProb, Class<? extends Item> superClass) {
        if (!dynamicCategories.containsKey(name)) {
            dynamicCategories.put(name, new DynamicCategory(name, firstProb, secondProb, superClass));
        }
        return dynamicCategories.get(name);
    }
    
    /**
     * Get a category if it exists
     */
    public static DynamicCategory get(String name) {
        return dynamicCategories.get(name);
    }
    
    /**
     * Get all registered categories
     */
    public static List<DynamicCategory> getAllCategories() {
        return new ArrayList<>(dynamicCategories.values());
    }
} 