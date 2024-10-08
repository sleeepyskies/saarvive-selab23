package de.unisaarland.cs.se.selab.simulation

import de.unisaarland.cs.se.selab.dataClasses.bases.Base
import de.unisaarland.cs.se.selab.dataClasses.emergencies.Emergency
import de.unisaarland.cs.se.selab.dataClasses.events.Construction
import de.unisaarland.cs.se.selab.dataClasses.events.Event
import de.unisaarland.cs.se.selab.dataClasses.events.RoadClosure
import de.unisaarland.cs.se.selab.dataClasses.events.RushHour
import de.unisaarland.cs.se.selab.dataClasses.events.TrafficJam
import de.unisaarland.cs.se.selab.dataClasses.events.VehicleUnavailable
import de.unisaarland.cs.se.selab.dataClasses.vehicles.Vehicle
import de.unisaarland.cs.se.selab.global.Log
import de.unisaarland.cs.se.selab.graph.Graph
import de.unisaarland.cs.se.selab.graph.Road
import de.unisaarland.cs.se.selab.graph.Vertex
import de.unisaarland.cs.se.selab.parser.AssetParser
import de.unisaarland.cs.se.selab.parser.CountyParser
import de.unisaarland.cs.se.selab.parser.EventsParser
import de.unisaarland.cs.se.selab.parser.SimulationParser
import io.github.oshai.kotlinlogging.KotlinLogging

/**
 * Is responsible for calling the parsers, cross
 * validating and constructing the DataHolder and Simulation
 */
class SimulationObjectConstructor(
    private val countyFile: String,
    private val assetFile: String,
    private val simulationFile: String,
    private val maxTick: Int?,
) {
    /**
     * Creates and returns a parsed and validated Simulation object
     */
    fun createSimulation(): Simulation? {
        val countyParser: CountyParser
        val graph: Graph

        val assetParser: AssetParser
        val bases: List<Base>
        val vehicles: List<Vehicle>

        val simulationParser: SimulationParser
        val eventsParser: EventsParser
        val emergencies: List<Emergency>
        val events: List<Event>
        try {
            // parse, validate and create map
            countyParser = CountyParser(countyFile)
            graph = countyParser.parse()

            // parse, validate and create assets
            assetParser = AssetParser("assets.schema", assetFile)
            val (parsedVehiclesList, parsedBasesList) = assetParser.parse()
            bases = parsedBasesList
            vehicles = parsedVehiclesList
            crossAssets(graph, bases, assetParser)

            // parse, validate and create events and emergencies
            simulationParser = SimulationParser("simulation.schema", simulationFile, graph)
            simulationParser.parse()
            emergencies = simulationParser.parsedEmergencies
            eventsParser = EventsParser("simulation.schema", simulationFile, vehicles)
            eventsParser.parse()
            events = eventsParser.parsedEvents
            crossSimulation(graph, emergencies, simulationParser)
            crossEvents(graph, events, vehicles, eventsParser)
        } catch (_: IllegalArgumentException) {
            KotlinLogging.logger("SimulationObjectConstructor: createSimulation()").error {
                "Invalid simulation"
            }
            return null
        }

        // init vehicles lastVisitedVertex
        for (vehicle in vehicles) {
            val base = bases.find { base: Base -> base.baseID == vehicle.assignedBaseID }
            if (base != null) {
                graph.graph.find { vertex: Vertex -> vertex.id == base.vertexID }.also {
                    if (it != null) {
                        vehicle.lastVisitedVertex = it
                    }
                }
            }
        }

        return if (validateEmergenciesBasedOnGraph(graph, emergencies) &&
            validateEventsBasedOnGraph(graph, events, vehicles) &&
            validateAssetsBasedOnGraph(graph, bases)
        ) {
            // If validation succeeds return simulation
            val dataHolder = DataHolder(graph, bases, events.toMutableList(), emergencies)
            Simulation(dataHolder, maxTick)
        } else {
            null
        }
    }
    private fun crossSimulation(graph: Graph, emgs: List<Emergency>, simParser: SimulationParser) {
        // cross validates the emergencies based on the graph
        if (!validateEmergenciesBasedOnGraph(graph, emgs)) {
            KotlinLogging.logger("SimulationObjectConstructor: crossSimulation()").error {
                "Invalid emergencies based on map"
            }
            Log.displayInitializationInfoInvalid(simParser.fileName)
            throw IllegalArgumentException("Invalid emergencies")
        }
    }

    private fun crossEvents(graph: Graph, events: List<Event>, vhcls: List<Vehicle>, evParser: EventsParser) {
        // cross validates the events based on the graph
        if (validateEventsBasedOnGraph(graph, events, vhcls)) {
            Log.displayInitializationInfoValid(evParser.fileName)
        } else {
            KotlinLogging.logger("SimulationObjectConstructor: crossEvents()").error { "Invalid events based on map" }
            Log.displayInitializationInfoInvalid(evParser.fileName)
            throw IllegalArgumentException("Invalid events")
        }
    }

    private fun crossAssets(graph: Graph, bases: List<Base>, assetParser: AssetParser) {
        // cross validates the assets based on the graph
        if (validateAssetsBasedOnGraph(graph, bases)) {
            Log.displayInitializationInfoValid(assetParser.fileName)
        } else {
            KotlinLogging.logger("SimulationObjectConstructor: crossAssets()").error { "Invalid assets based on map" }
            Log.displayInitializationInfoInvalid(assetParser.fileName)
            throw IllegalArgumentException("Invalid assets")
        }
    }

    /**
     * Cross validates the assets based on the graph
     */
    private fun validateAssetsBasedOnGraph(graph: Graph, bases: List<Base>): Boolean {
        // Init map of each vertex to a list of bases located on it
        val mapping: MutableMap<Vertex, MutableList<Base>> = mutableMapOf()
        for (vertex in graph.graph) {
            mapping[vertex] = mutableListOf()
        }

        // Check each base exists on the map
        for (base in bases) {
            // find base vertex
            val baseVertex = graph.graph.find { vertex: Vertex -> vertex.id == base.vertexID }
            // add base to mapping
            if (baseVertex != null) {
                // add base to mapping
                mapping[baseVertex]?.add(base)
            } else {
                KotlinLogging.logger(
                    "SimulationObjectConstructor: validateAssetsBasedOnGraph()"
                ).error { "Base does not exist on map" }
                return false
            }
        }

        // Check each vertex has at most one base on it
        for ((_, baseList) in mapping) {
            if (baseList.size > 1) return false
        }
        return true
    }

    /**
     * Cross validates the events based on the graph
     */
    private fun validateEventsBasedOnGraph(graph: Graph, events: List<Event>, vehicles: List<Vehicle>): Boolean {
        for (event in events) {
            when (event) {
                is VehicleUnavailable -> if (!validateVehicleEvent(event, vehicles)) {
                    KotlinLogging.logger(
                        "SimulationObjectConstructor: validateEventsBasedOnGraph()"
                    ).error { "Vehicle does not exist on map" }
                    return false
                }
                is Construction -> if (!validateOneWayStreet(event, graph)) {
                    KotlinLogging.logger(
                        "SimulationObjectConstructor: validateEventsBasedOnGraph"
                    )
                        .error { "One way street does not exist on map" }
                    return false
                }
                else -> if (!validateGraphEvent(event, graph)) return false
            }
        }
        return true
    }

    /** Helper method for validateEventsBasedOnGraph(). Validates if oneWay street street does not exist */
    private fun validateOneWayStreet(event: Construction, graph: Graph): Boolean {
        val vertex1 = graph.graph.find { vertex: Vertex -> vertex.id == event.sourceID }
        val vertex2 = graph.graph.find { vertex: Vertex -> vertex.id == event.targetID }
        val road = vertex1?.connectingRoads?.get(vertex2?.id)
        val road2 = vertex2?.connectingRoads?.get(vertex1?.id)
        return road != null || road2 != null
    }

    /**
     * Helper method for validateEventsBasedOnGraph(). Validates if the events vehicle ID exists
     */
    private fun validateVehicleEvent(event: VehicleUnavailable, vehicles: List<Vehicle>): Boolean {
        val vehicle = vehicles.find { vehicle: Vehicle -> vehicle.id == event.vehicleID }
        return vehicle != null
    }

    /**
     * Helper method for validateEventsBasedOnGraph(). Validates if the roads exist
     */
    private fun validateGraphEvent(event: Event, graph: Graph): Boolean {
        return when (event) {
            is RushHour -> true
            is Construction -> roadExists(event.sourceID, event.targetID, graph) ||
                roadExists(event.targetID, event.sourceID, graph)
            is RoadClosure -> roadExists(event.sourceID, event.targetID, graph) ||
                roadExists(event.targetID, event.sourceID, graph)
            is TrafficJam -> roadExists(event.startVertex, event.endVertex, graph) ||
                roadExists(event.endVertex, event.startVertex, graph)
            else -> throw IllegalArgumentException("Unsupported event type: ${event::class.simpleName}")
        }
    }

    /**
     * Checks if the vertices and road exists, as well as checking the vertices are connected via the edge
     */
    private fun roadExists(sourceID: Int, targetID: Int, graph: Graph): Boolean {
        // Find the source and target vertices
        val sourceVertex = graph.graph.find { vertex -> vertex.id == sourceID }
        val targetVertex = graph.graph.find { vertex -> vertex.id == targetID }

        // Check if source and target vertices exist
        if (sourceVertex == null || targetVertex == null) {
            KotlinLogging.logger("SimulationObjectConstructor:").error {
                "Source or target vertex does not exist on map"
            }
            return false
        }

        // Check if there's a connecting road from the source to the target
        return sourceVertex.connectingRoads.containsKey(targetID)
    }

    /**
     * Checks that the road name and village name correspond to a road in the map
     */
    private fun getRoad(roadName: String, villageName: String, graph: Graph): Road? {
        return graph.roads.find { road: Road -> road.roadName == roadName && road.villageName == villageName }
    }

    /**
     * Cross validates the emergencies based on the graph
     * Needs to validate:
     *      - if check the emergency's road name and village name correspond to a road
     *      - two emergencies don't happen at the same time on the same road
     */
    private fun validateEmergenciesBasedOnGraph(graph: Graph, emergencies: List<Emergency>): Boolean {
        // init mapping
        val mapping: MutableMap<Road, MutableList<Emergency>> = mutableMapOf()
        for (road in graph.roads) {
            mapping[road] = mutableListOf()
        }

        // check each emergency road exists
        for (emergency in emergencies) {
            // get emergency road anc check it exists
            val emergencyRoad = getRoad(emergency.roadName, emergency.villageName, graph)

            // add emergency to the mapping
            if (emergencyRoad != null) {
                // add base to mapping
                mapping[emergencyRoad]?.add(emergency)
            } else {
                KotlinLogging.logger("SimulationObjectConstructor:").error { "Emergency road does not exist on map" }
                return false
            }
        }

        // check two emergencies at the same location do not occur at the same time
        // loop over all emergencyLists
        for ((_, emergencyList) in mapping) {
            if (!checkOverlappingEmergencies(emergencyList)) return false
        }

        return true
    }

    /**
     * Checks that no two emergencies in the given list overlap in their duration
     */
    private fun checkOverlappingEmergencies(emergencyList: List<Emergency>): Boolean {
        for (emergencyOne in emergencyList) {
            for (emergencyTwo in emergencyList) {
                if (emergencyOne != emergencyTwo && isInRange(emergencyOne, emergencyTwo)) {
                    return false
                }
            }
        }
        return true
    }

    private fun isInRange(emergencyOne: Emergency, emergencyTwo: Emergency): Boolean {
        return emergencyTwo.startTick >= emergencyOne.startTick &&
            emergencyTwo.startTick < emergencyOne.startTick + emergencyOne.handleTime
    }
}
