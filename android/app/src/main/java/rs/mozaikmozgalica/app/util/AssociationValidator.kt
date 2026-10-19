package rs.mozaikmozgalica.app.util

import rs.mozaikmozgalica.app.data.AssociationGrid

object AssociationValidator {

    data class ValidationResult(
        val isValid: Boolean,
        val errors: List<String>
    )

    fun validate(grid: AssociationGrid): ValidationResult {
        val errors = mutableListOf<String>()

        fun checkCol(colName: String, clues: List<String>, sol: String, finalSol: String) {
            if (clues.size != 4) {
                errors.add("Grid ${grid.id} Col $colName must have exactly 4 clues.")
            }
            val solUpper = sol.trim().uppercase()
            val finalUpper = finalSol.trim().uppercase()

            if (solUpper == finalUpper) {
                errors.add("Grid ${grid.id} Col $colName solution '$sol' cannot equal final solution '$finalSol'.")
            }

            clues.forEach { clue ->
                val clueUpper = clue.trim().uppercase()
                if (clueUpper == solUpper) {
                    errors.add("Grid ${grid.id} Col $colName clue '$clue' cannot equal column solution '$sol'.")
                }
                if (clueUpper == finalUpper) {
                    errors.add("Grid ${grid.id} Col $colName clue '$clue' cannot equal final solution '$finalSol'.")
                }
            }
        }

        checkCol("A", grid.colAClues, grid.colASolution, grid.finalSolution)
        checkCol("B", grid.colBClues, grid.colBSolution, grid.finalSolution)
        checkCol("C", grid.colCClues, grid.colCSolution, grid.finalSolution)
        checkCol("D", grid.colDClues, grid.colDSolution, grid.finalSolution)

        return ValidationResult(isValid = errors.isEmpty(), errors = errors)
    }

    fun validateAll(grids: List<AssociationGrid>): List<String> {
        val allErrors = mutableListOf<String>()
        grids.forEach { grid ->
            val res = validate(grid)
            if (!res.isValid) {
                allErrors.addAll(res.errors)
            }
        }
        return allErrors
    }
}
