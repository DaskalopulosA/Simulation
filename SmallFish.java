import java.util.List;
import java.util.Iterator;

/**
 * A simple model of a small fish.
 * Small fish age, move, feed, breed, and die.
 * 
 * @author David J. Barnes, Michael Kölling, Ivan Arabadzhiev, Adonis Daskalopulos
 * @version 2021.03.03
 */
public class SmallFish extends Animal
{
    // Characteristics shared by all small fish (class variables).

    // The age at which a small fish can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age at which a small fish starts to have a chance of dying of age.
    private static final int AGE_OF_DECAY = 30;
    // The rate of change of death probability.
    private static final double RATE_OF_DECAY = 0.1;
    // The likelihood of a small fish mating.
    private static final double IMPREGNATION_PROBABILITY = 0.12;
    // The minimun of steps before next pregnancy.
    private static final int PREGNANCY_PERIOD = 1;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    // The probability of a female meeting a male.
    private static final double MALE_TO_FEMALE_RATIO = 0.5;

    /**
     * Create a new small fish. A small fish is created with age
     * zero (a new born). The gender is randomly decided.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public SmallFish(Field field, Location location)
    {
        super(field, location);
        if(getRandom().nextDouble() <= MALE_TO_FEMALE_RATIO){
            changeGender();
        }
    }   

    /**
     * This is what the small fish does most of the time - it swims 
     * around. It will search for a mate, breed or die of old age.
     * 
     * @param newSmallFish A list to return newly hatched small fish.
     */
    public void act(List<Organism> newSmallFish)
    {
        incrementAge(AGE_OF_DECAY, RATE_OF_DECAY);
        if(isAlive()) {
            findMate(newSmallFish);
            // Try to move into a free location.
            Location newLocation = getField().freeAdjacentLocation(getLocation());
            if(newLocation != null) {
                setLocation(newLocation);
            }
            else {
                // Overcrowding.
                setDead();
            }
        }
    }

    /**
     * The process of a small fish finding a mate of the same species
     * and of the opposite gender.
     *
     * @param  newSmallFish  A list to return newly hatched small fish.
     */
    private void findMate(List<Organism> newSmallFish)
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof SmallFish) {
                SmallFish mate = (SmallFish) animal;
                boolean mateGender = mate.checkFemale();
                if(mate.isAlive() && checkFemale() == !mateGender) { 
                    giveBirth(newSmallFish, litterSize());
                }
            }
        }
    }

    /**
     * Check whether or not this small fish is to give birth at this step.
     * New births will be made into free adjacent locations.
     * 
     * @param newSmallFish A list to return newly hatched small fish.
     * @param litterSize 
     */
    private void giveBirth(List<Organism> newSmallFish, int litterSize)
    {
        // New small fish are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        for(int b = 0; b < litterSize && free.size() > 0; b++) {
            Location loc = free.remove(0);
            SmallFish young = new SmallFish(field, loc);
            newSmallFish.add(young);
        }
    }
    
    /**
     * Holds the generated number, representing the number of births.
     * 
     * @return 
     */
    private int litterSize()
    {
        return impregnate(BREEDING_AGE, MAX_LITTER_SIZE, PREGNANCY_PERIOD, 
                          IMPREGNATION_PROBABILITY);
    }
}