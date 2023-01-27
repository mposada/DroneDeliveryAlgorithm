import java.io.File

data class Drone(val name: String, val maxWeight: Int)
data class Location(val name: String, val weight: Int)
fun main(args: Array<String>) {

    val drones = arrayListOf<Drone>()
    val locations = arrayListOf<Location>()

    // extract data from input file
    val file = File("asset/input.txt")
    file.forEachLine { line ->
        val elements = line.split(", ")
        if (elements[0].contains("Drone")) {
            val dronesCount = elements.size / 2
            var pairIndex = 0
            for (n in 0 until dronesCount) {
                val droneName = elements[pairIndex].removePrefix("[").removeSuffix("]")
                val droneMaxWeight = elements[(pairIndex + 1)].removePrefix("[").removeSuffix("]").toInt()
                val drone = Drone(droneName, droneMaxWeight)
                drones.add(drone)
                pairIndex += 2
            }
        } else {
            val locationWeight = elements[1].removePrefix("[").removeSuffix("]").toInt()
            val locationName = elements[0].removePrefix("[").removeSuffix("]")
            val location = Location(locationName, locationWeight)
            locations.add(location)
        }
    }

    val result = distributeDeliveries(drones, locations)
    println(result)
}

fun distributeDeliveries(drones: ArrayList<Drone>, locations: ArrayList<Location>): MutableMap<Drone, Map<String, ArrayList<Location>>> {
    // 1. distributes the deliveries among the drones using a greedy approach

    // Sort the locations by weight in descending order
    locations.sortBy { it.weight }
    locations.sortBy { it.weight }
    // Sort the drones by weight in ascending order
    drones.sortByDescending { it.maxWeight }

    // Initialize an empty list to store the assigned deliveries for each drone
    val droneDeliveries = Array(drones.size) { mutableListOf<Location>() }

    var i = 0
    var j = 0
    while (i < locations.size && j < drones.size) {

        val location = locations[i]
        val drone = drones[j]

        // If the current drone can carry the current location's weight, assign the location to the drone
        if (drone.maxWeight >= location.weight) {
            droneDeliveries[j].add(location)
            i++
        }
        j++
        // If we've reached the end of the drones list, start again from the first drone
        if (j == drones.size) {
            j = 0
        }
    }

    // 2. set up the trips for each drone after the deliveries have been distributed
    val droneTrips: MutableMap<Drone, Map<String, ArrayList<Location>>> = mutableMapOf()
    for (delivery in droneDeliveries.indices) {
        //println("Drone ${drones[delivery].name} is assigned to deliver:")

        var sum = 0
        var trip = 1
        // will store trips
        val additions: MutableMap<String, ArrayList<Location>> = mutableMapOf()

        for ((currentIndex, location) in droneDeliveries[delivery].withIndex()) {
            sum += droneDeliveries[delivery][currentIndex].weight
            if (sum <= drones[delivery].maxWeight) {

                var existingList = additions["trip#$trip"]
                if (existingList != null) {
                    existingList.add(droneDeliveries[delivery][currentIndex])
                } else {
                    existingList = arrayListOf(droneDeliveries[delivery][currentIndex])
                }

                additions["trip#$trip"] = existingList
            } else {
                // increase current trip
                trip++
                additions["trip#$trip"] = arrayListOf(droneDeliveries[delivery][currentIndex])
                // increase next trip
                trip++
                // reset addition
                sum = 0
            }
        }

        droneTrips[drones[delivery]] = additions

    }

    return droneTrips
}