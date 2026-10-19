package rs.mozaikmozgalica.app.data

object SerbianTransliteration {
    private val latToCirMap = mapOf(
        'A' to 'А', 'a' to 'а', 'B' to 'Б', 'b' to 'б', 'V' to 'В', 'v' to 'в',
        'G' to 'Г', 'g' to 'г', 'D' to 'Д', 'd' to 'д', 'Đ' to 'Ђ', 'đ' to 'ђ',
        'E' to 'Е', 'e' to 'е', 'Ž' to 'Ж', 'ž' to 'ж', 'Z' to 'З', 'z' to 'з',
        'I' to 'И', 'i' to 'и', 'J' to 'Ј', 'j' to 'ј', 'K' to 'К', 'k' to 'к',
        'L' to 'Л', 'l' to 'л', 'M' to 'М', 'm' to 'м', 'N' to 'Н', 'n' to 'н',
        'O' to 'О', 'o' to 'о', 'P' to 'П', 'p' to 'п', 'R' to 'Р', 'r' to 'р',
        'S' to 'С', 's' to 'с', 'T' to 'Т', 't' to 'т', 'Ć' to 'Ћ', 'ć' to 'ћ',
        'U' to 'У', 'u' to 'у', 'F' to 'Ф', 'f' to 'ф', 'H' to 'Х', 'h' to 'х',
        'C' to 'Ц', 'c' to 'ц', 'Č' to 'Ч', 'č' to 'ч', 'Š' to 'Ш', 'š' to 'ш'
    )

    fun toCyrillic(text: String): String {
        if (text.isEmpty()) return ""
        var res = text
        res = res.replace("DŽ", "Џ").replace("Dž", "Џ").replace("dž", "џ")
        res = res.replace("LJ", "Љ").replace("Lj", "Љ").replace("lj", "љ")
        res = res.replace("NJ", "Њ").replace("Nj", "Њ").replace("nj", "њ")

        val sb = StringBuilder()
        for (char in res) {
            sb.append(latToCirMap[char] ?: char)
        }
        return sb.toString()
    }

    fun applyScript(text: String, scriptType: ScriptType): String {
        return if (scriptType == ScriptType.CIRILICA) toCyrillic(text) else text
    }
}
