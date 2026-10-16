package com.windingmaster.app.domain.engine

/**
 * موتور محاسباتی سیم‌پیچی موتورهای الکتریکی
 * بر اساس فرمول‌های استاندارد سیم‌پیچی و مراجع فنی
 */
data class WindingResult(
    val z: Int,                     // تعداد شیارها
    val poles: Int,                 // تعداد قطب‌ها (2p)
    val polePairs: Int,             // تعداد جفت قطب (P)
    val phases: Int,                // تعداد فازها
    val electricalAngle: Double,    // AEZ: زاویه الکتریکی هر شیار
    val slotsPerPolePerPhase: Double, // q: شیار در هر قطب در هر فاز
    val polePitch: Double,          // T: گام قطبی
    val synchronousSpeed: Int,      // N: سرعت سنکرون (دور در دقیقه)
    val phaseStarts: List<Int>,     // V, W: شروع فاز دوم و سوم
    val windingStep: Int,           // g: گام سیم‌بندی
    val windingStepReduced: Int,    // g': گام سیم‌بندی کوتاه‌شده
    val coilPitchY1: Int,           // Y1: گام اول کویل
    val coilPitchY2: Int,           // Y2: گام دوم کویل
    val coilGroups: List<List<Int>>, // گروه‌های کویل برای هر فاز
    val frequency: Int = 50         // فرکانس شبکه (Hz)
)

class WindingCalculator {

    /**
     * محاسبه کامل پارامترهای سیم‌پیچی
     * @param slots تعداد شیارها (Z)
     * @param poles تعداد قطب‌ها (2p)
     * @param phases تعداد فازها (1 یا 3)
     * @param frequency فرکانس شبکه (پیش‌فرض ۵۰ هرتز برای ایران)
     */
    fun calculate(slots: Int, poles: Int, phases: Int, frequency: Int = 50): WindingResult {
        require(slots > 0 && poles > 0) { "تعداد شیار و قطب باید بزرگتر از صفر باشد" }
        require(slots % 2 == 0) { "تعداد شیارها باید زوج باشد" }
        require(poles % 2 == 0) { "تعداد قطب‌ها باید زوج باشد" }
        require(phases == 1 || phases == 3) { "تعداد فازها باید ۱ یا ۳ باشد" }

        val polePairs = poles / 2  // P = تعداد جفت قطب

        // AEZ = 360 × P / Z  →  زاویه الکتریکی هر شیار
        val electricalAngle = 360.0 * polePairs / slots

        // q = Z / (2P × 3)  →  شیار در هر قطب در هر فاز
        val slotsPerPolePerPhase = slots.toDouble() / (poles * phases)

        // T = Z / 2P  →  گام قطبی
        val polePitch = slots.toDouble() / poles

        // N = 60 × F / P  →  سرعت سنکرون
        val synchronousSpeed = (60 * frequency) / polePairs

        // V = 120 / AEZ + 1  →  شروع فاز دوم
        // W = 240 / AEZ + 1  →  شروع فاز سوم
        val phaseStarts = mutableListOf<Int>()
        if (phases == 3) {
            phaseStarts.add(1)
            phaseStarts.add((120.0 / electricalAngle).toInt() + 1)
            phaseStarts.add((240.0 / electricalAngle).toInt() + 1)
        }

        // g = مقدار صحیح گام سیم‌بندی
        val windingStep = polePitch.toInt()
        val windingStepReduced = windingStep - 2

        // Y1, Y2: گام اول و دوم کویل (برای سیم‌پیچی دولایه و کنسنتریک)
        val coilPitchY1 = windingStep
        val coilPitchY2 = windingStep - 2

        // تخصیص گروه‌های کویل به فازها
        val coilGroups = assignCoilGroups(slots, poles, phases)

        return WindingResult(
            z = slots,
            poles = poles,
            polePairs = polePairs,
            phases = phases,
            electricalAngle = electricalAngle,
            slotsPerPolePerPhase = slotsPerPolePerPhase,
            polePitch = polePitch,
            synchronousSpeed = synchronousSpeed,
            phaseStarts = phaseStarts,
            windingStep = windingStep,
            windingStepReduced = windingStepReduced,
            coilPitchY1 = coilPitchY1,
            coilPitchY2 = coilPitchY2,
            coilGroups = coilGroups,
            frequency = frequency
        )
    }

    /**
     * تخصیص شیارها به گروه‌های کویل هر فاز
     * منطق: توزیع یکنواخت شیارها بر اساس زاویه الکتریکی
     */
    private fun assignCoilGroups(slots: Int, poles: Int, phases: Int): List<List<Int>> {
        val groups = MutableList(phases) { mutableListOf<Int>() }
        for (slot in 1..slots) {
            // محاسبه زاویه الکتریکی این شیار
            val angle = ((slot - 1) * 360.0 * (poles / 2) / slots) % 360.0
            // تعیین فاز بر اساس زاویه (۰-۱۲۰: U، ۱۲۰-۲۴۰: V، ۲۴۰-۳۶۰: W)
            val phaseIndex = when {
                phases == 1 -> 0
                angle < 120 -> 0
                angle < 240 -> 1
                else -> 2
            }
            groups[phaseIndex].add(slot)
        }
        return groups
    }
}
