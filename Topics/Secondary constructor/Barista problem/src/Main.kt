class EspressoMachine {
    val costPerServing: Float

    constructor(coffeeCapsulesCount: Int, totalCost: Float) {
        if (coffeeCapsulesCount <= 0) {
            throw IllegalArgumentException("Coffee capsule count must be positive")
        }
        if (totalCost <= 0) {
            throw IllegalArgumentException("Total cost must be positive")
        }
        this.costPerServing = totalCost / coffeeCapsulesCount
    }
    constructor(coffeeBeansWeight: Float, totalCost: Float) {
        if (coffeeBeansWeight <= 0) {
            throw IllegalArgumentException("Weight must be positive")
        }
        if (totalCost < 0) {
            throw IllegalArgumentException("Cost must be non-negative")
        }

        this.costPerServing = totalCost / (coffeeBeansWeight / 10)
    }
}
