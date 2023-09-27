package de.unisaarland.cs.se.selab.dataClasses.emergencies

/**
 * Enumeration class with all statuses that emergency van be assigned to
 */
enum class EmergencyStatus {
    UNASSIGNED, // no base is responsible yet (changes when the tick comes)
    ASSIGNED, // base chosen, not calculated needed assets yet
    ONGOING, /* base chosen, calculated needed assets; assets may be on their way there
    (doesn't mean ALL required assets, need to check the list of required and their status)*/
    HANDLING, // all assets at the place handling it
    RESOLVED, // finished successfully
    FAILED // failed and cannot be resolved anymore
}
