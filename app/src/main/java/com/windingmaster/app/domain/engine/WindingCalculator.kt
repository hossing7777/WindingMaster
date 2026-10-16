package com.windingmaster.app.domain.engine
data class WindingResult(val polePitch: Int, val slotAngleElectrical: Double, val slotsPerPolePerPhase: Double, val coilGroups: List<List<Int>>)
class WindingCalculator {
    fun calculate(slots: Int, poles: Int, phases: Int): WindingResult {
        require(slots > 0 && poles > 0) { "تعداد شیار و قطب باید بزرگتر از صفر باشد" }
        require(slots % 2 == 0) { "تعداد شیارها باید زوج باشد" }
        val polePitch = slots / poles
        val slotAngleElectrical = 180.0 / slots
        val slotsPerPolePerPhase = slots.toDouble() / (poles * phases)
        val coilGroups = mutableListOf<List<Int>>()
        val slotsPerPhase = slots / phases
        for (i in 0 until phases) { coilGroups.add(((i * slotsPerPhase + 1)..((i + 1) * slotsPerPhase)).toList()) }
        return WindingResult(polePitch, slotAngleElectrical, slotsPerPolePerPhase, coilGroups)
    }
}
