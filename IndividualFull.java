// Placeholder for CollectionTerritory class - needed for compilation
// In a real scenario, this would be the actual CollectionTerritory class
// You MUST replace this with your actual CollectionTerritory class or ensure
// the necessary getter methods exist in your actual class.
class CollectionTerritory {
    // Assume getters for all parameters mentioned in pasted_content.txt
    // Example values are used here for demonstration.
    public double getMc() { return 2028; } // Midpoint year for composting adoption
    public double getMs() { return 2029; } // Midpoint year for sorting adoption
    public double getMpf() { return 2030; } // Midpoint year for food waste reduction adoption
    public double getMpg() { return 2031; } // Midpoint year for green waste reduction adoption
    public double getAlphaCfInitial() { return 0.1; } // Initial intention: compost food
    public double getAlphaCgInitial() { return 0.1; } // Initial intention: compost green
    public double getAlphaSfInitial() { return 0.1; } // Initial intention: sort food
    public double getAlphaSgInitial() { return 0.1; } // Initial intention: sort green
    public double getAlphaCfMax() { return 1.0; }     // Max intention: compost food
    public double getAlphaCgMax() { return 1.0; }     // Max intention: compost green
    public double getAlphaSfMax() { return 1.0; }     // Max intention: sort food
    public double getAlphaSgMax() { return 1.0; }     // Max intention: sort green
    public double getAlphaPfTarget() { return 0.5; }  // Target reduction level: food
    public double getAlphaPgTarget() { return 0.4; }  // Target reduction level: green
    // Add getters for other relevant parameters if needed by your model
}

/**
 * Represents an individual within a collection territory in the BEECOME2 model.
 * This class calculates individual waste production based on base rates and
 * evolving behavioral intentions (sorting, composting, prevention) driven by
 * sigmoid functions based on territory-level parameters.
 */
public class Individual {

    private CollectionTerritory territory; // Reference to the parent territory
    private double baseFoodWaste; // Base food waste production per individual (e.g., kg/year)
    private double baseGreenWaste; // Base green waste production per individual (e.g., kg/year)

    // Current behavioral intentions (alpha values, typically 0 to 1)
    private double alpha_cf; // Intention to sort food waste for home composting
    private double alpha_cg; // Intention to sort green waste for home composting
    private double alpha_sf; // Intention to sort food waste for dedicated collection
    private double alpha_sg; // Intention to sort green waste for dedicated collection
    private double alpha_pf; // Intention to reduce food waste (prevention)
    private double alpha_pg; // Intention to reduce green waste (prevention)

    // Sigmoid steepness parameter - controls how quickly intentions change around the midpoint year.
    // This might need tuning based on model calibration or specific requirements.
    private static final double SIGMOID_STEEPNESS = 0.5; // Example value

    /**
     * Constructor for the Individual class.
     * Initializes the individual with base waste rates and associates it with a territory.
     * Initial behavioral intentions are taken from the territory's starting values.
     *
     * @param territory The CollectionTerritory this individual belongs to. Cannot be null.
     * @param baseFoodWaste Initial base food waste production rate for this individual.
     * @param baseGreenWaste Initial base green waste production rate for this individual.
     */
    public Individual(CollectionTerritory territory, double baseFoodWaste, double baseGreenWaste) {
        if (territory == null) {
            throw new IllegalArgumentException("CollectionTerritory cannot be null.");
        }
        this.territory = territory;
        this.baseFoodWaste = baseFoodWaste;
        this.baseGreenWaste = baseGreenWaste;

        // Initialize sorting/composting intentions based on territory's initial values
        this.alpha_cf = territory.getAlphaCfInitial();
        this.alpha_cg = territory.getAlphaCgInitial();
        this.alpha_sf = territory.getAlphaSfInitial();
        this.alpha_sg = territory.getAlphaSgInitial();

        // Initialize prevention intentions. Assuming they start at 0 and evolve.
        // The global 'einit' parameter might affect the starting point elsewhere in the model.
        this.alpha_pf = 0.0;
        this.alpha_pg = 0.0;
    }

    /**
     * Updates the behavioral intentions (alpha values) of the individual based on the
     * current simulation year using sigmoid functions.
     * The parameters for the sigmoid functions (midpoint year, max value) are retrieved
     * from the associated CollectionTerritory.
     *
     * @param currentYear The current year in the simulation.
     */
    public void updateBehavioralIntentions(int currentYear) {
        // Update composting intentions (food & green waste)
        // Assumes 'mc' is the midpoint for both food and green waste composting adoption
        this.alpha_cf = calculateSigmoid(currentYear, territory.getMc(), territory.getAlphaCfMax(), SIGMOID_STEEPNESS);
        this.alpha_cg = calculateSigmoid(currentYear, territory.getMc(), territory.getAlphaCgMax(), SIGMOID_STEEPNESS);

        // Update sorting intentions (food & green waste)
        // Assumes 'ms' is the midpoint for both food and green waste sorting adoption
        this.alpha_sf = calculateSigmoid(currentYear, territory.getMs(), territory.getAlphaSfMax(), SIGMOID_STEEPNESS);
        this.alpha_sg = calculateSigmoid(currentYear, territory.getMs(), territory.getAlphaSgMax(), SIGMOID_STEEPNESS);

        // Update prevention intentions (food & green waste)
        // Uses 'mpf'/'mpg' as midpoints and 'alphaPfTarget'/'alphaPgTarget' as max values
        this.alpha_pf = calculateSigmoid(currentYear, territory.getMpf(), territory.getAlphaPfTarget(), SIGMOID_STEEPNESS);
        this.alpha_pg = calculateSigmoid(currentYear, territory.getMpg(), territory.getAlphaPgTarget(), SIGMOID_STEEPNESS);
    }

    /**
     * Calculates the value of a standard logistic (sigmoid) function.
     * Formula: f(x) = max / (1 + exp(-k * (x - midpoint)))
     * This function models the adoption rate of behaviors over time.
     *
     * @param x The current input value (e.g., simulation year).
     * @param midpoint The value of x at which the function reaches half of its max value.
     * @param max The maximum value (asymptote) of the function.
     * @param k The steepness or growth rate of the curve.
     * @return The calculated sigmoid value, typically between 0 and max.
     */
    private double calculateSigmoid(double x, double midpoint, double max, double k) {
        // Handle cases where max is zero or negative to avoid nonsensical results
        if (max <= 0) {
            return 0.0;
        }
        // Calculate the exponent term
        double exponent = -k * (x - midpoint);
        // Calculate the sigmoid value
        // Add a small epsilon in denominator potentially? Usually not needed with doubles.
        return max / (1.0 + Math.exp(exponent));
    }

    /**
     * Calculates the actual amount of food waste produced by the individual in the current step.
     * This is the base food waste adjusted by the food waste prevention intention (alpha_pf).
     * Assumes alpha_pf represents the proportion of base waste that is *prevented*.
     *
     * @return The calculated current food waste amount for this individual.
     */
    public double calculateCurrentFoodWaste() {
        // Ensure the reduction factor derived from alpha_pf is clamped between 0 and 1
        double reductionFactor = Math.max(0.0, Math.min(1.0, this.alpha_pf));
        // Current waste = Base waste * (1 - reduction factor)
        return this.baseFoodWaste * (1.0 - reductionFactor);
    }

    /**
     * Calculates the actual amount of green waste produced by the individual in the current step.
     * This is the base green waste adjusted by the green waste prevention intention (alpha_pg).
     * Assumes alpha_pg represents the proportion of base waste that is *prevented*.
     *
     * @return The calculated current green waste amount for this individual.
     */
    public double calculateCurrentGreenWaste() {
        // Ensure the reduction factor derived from alpha_pg is clamped between 0 and 1
        double reductionFactor = Math.max(0.0, Math.min(1.0, this.alpha_pg));
        // Current waste = Base waste * (1 - reduction factor)
        return this.baseGreenWaste * (1.0 - reductionFactor);
    }

    // --- Standard Getters --- //

    public CollectionTerritory getTerritory() {
        return territory;
    }

    public double getBaseFoodWaste() {
        return baseFoodWaste;
    }

    public double getBaseGreenWaste() {
        return baseGreenWaste;
    }

    public double getAlphaCf() {
        return alpha_cf;
    }

    public double getAlphaCg() {
        return alpha_cg;
    }

    public double getAlphaSf() {
        return alpha_sf;
    }

    public double getAlphaSg() {
        return alpha_sg;
    }

    public double getAlphaPf() {
        return alpha_pf;
    }

    public double getAlphaPg() {
        return alpha_pg;
    }

    // --- Standard Setters (Optional - Add if needed) --- //

    public void setBaseFoodWaste(double baseFoodWaste) {
        this.baseFoodWaste = baseFoodWaste;
    }

    public void setBaseGreenWaste(double baseGreenWaste) {
        this.baseGreenWaste = baseGreenWaste;
    }

    /**
     * Main method for basic standalone testing of the Individual class logic.
     * Creates a dummy territory and an individual, then simulates the update
     * of behavioral intentions and waste calculation over several years.
     */
    public static void main(String[] args) {
        // 1. Create a dummy CollectionTerritory instance (using the placeholder class)
        CollectionTerritory testTerritory = new CollectionTerritory();

        // 2. Create an Individual instance
        // Example: Base 100 kg/year food waste, 50 kg/year green waste
        Individual individual = new Individual(testTerritory, 100.0, 50.0);

        System.out.println("--- Initial State ---");
        System.out.println("Territory Midpoints (mc, ms, mpf, mpg): " + testTerritory.getMc() + ", " + testTerritory.getMs() + ", " + testTerritory.getMpf() + ", " + testTerritory.getMpg());
        System.out.println("Territory Targets/Max (alpha_cf_max, alpha_sf_max, alpha_pf_target): " + testTerritory.getAlphaCfMax() + ", " + testTerritory.getAlphaSfMax() + ", " + testTerritory.getAlphaPfTarget());
        System.out.println("Individual Base Food Waste: " + individual.getBaseFoodWaste());
        System.out.println("Individual Base Green Waste: " + individual.getBaseGreenWaste());
        System.out.printf("Initial alpha_cf (Compost Food): %.4f (from territory initial: %.2f)
", individual.getAlphaCf(), testTerritory.getAlphaCfInitial());
        System.out.printf("Initial alpha_sf (Sort Food):    %.4f (from territory initial: %.2f)
", individual.getAlphaSf(), testTerritory.getAlphaSfInitial());
        System.out.printf("Initial alpha_pf (Prevent Food): %.4f (starts at 0.0)
", individual.getAlphaPf());
        System.out.printf("Initial Current Food Waste:  %.4f
", individual.calculateCurrentFoodWaste());
        System.out.printf("Initial Current Green Waste: %.4f
", individual.calculateCurrentGreenWaste());

        System.out.println("
--- Simulating Year Updates (2025-2035) ---");
        // 3. Simulate updating intentions over a range of years
        for (int year = 2025; year <= 2035; year++) {
            individual.updateBehavioralIntentions(year);
            System.out.println("
Year: " + year);
            System.out.printf("  alpha_cf (Compost Food): %.4f
", individual.getAlphaCf());
            System.out.printf("  alpha_cg (Compost Green): %.4f
", individual.getAlphaCg());
            System.out.printf("  alpha_sf (Sort Food):    %.4f
", individual.getAlphaSf());
            System.out.printf("  alpha_sg (Sort Green):   %.4f
", individual.getAlphaSg());
            System.out.printf("  alpha_pf (Prevent Food): %.4f
", individual.getAlphaPf());
            System.out.printf("  alpha_pg (Prevent Green):%.4f
", individual.getAlphaPg());
            System.out.printf("  Current Food Waste:      %.4f
", individual.calculateCurrentFoodWaste());
            System.out.printf("  Current Green Waste:     %.4f
", individual.calculateCurrentGreenWaste());
        }
    }
}

