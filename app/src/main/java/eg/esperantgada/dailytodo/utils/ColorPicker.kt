package eg.esperantgada.dailytodo.utils

object ColorPicker {

    private val colors = arrayOf(
        "#3EB9DF",
        "#3685BC",
        "#D36280",
        "#E44F55",
        "#FA8056",
        "#818BCA",
        "#7D659F",
        "#51BAB3",
        "#4FB66C",
        "#E3AD17",
        "#627991",
        "#EF8EAD"
    )

    var colorIndex = 0

    fun getColors() : String{
        colorIndex = (colorIndex + 1) % colors.size

        return colors[colorIndex]
    }
}