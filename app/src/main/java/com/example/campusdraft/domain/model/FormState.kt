package com.example.campusdraft.domain.model


/**
 * Holds the live editing state of the form screen.
 * [fieldValues] maps each FormFieldType to the user-entered text.
 */
data class FormState(
    val applicationType: ApplicationType,
    val fieldValues: Map<FormFieldType, String> = emptyMap()
) {
    /** Renders the template, replacing all {{PLACEHOLDER}} tokens with field values. */
    fun renderText(): String {
        var text = applicationType.template
        FormFieldType.values().forEach { field ->
            val value = fieldValues[field]?.ifBlank { "[${field.label}]" } ?: "[${field.label}]"
            text = text.replace("{{${field.toToken()}}}", value)
        }
        return text
    }

    private fun FormFieldType.toToken(): String = when (this) {
        FormFieldType.STUDENT_NAME         -> "STUDENT_NAME"
        FormFieldType.ENROLLMENT_NUMBER    -> "ENROLLMENT_NUMBER"
        FormFieldType.CLASS_SEMESTER       -> "CLASS_SEMESTER"
        FormFieldType.DEPARTMENT           -> "DEPARTMENT"
        FormFieldType.DATE                 -> "DATE"
        FormFieldType.TEACHER_NAME         -> "TEACHER_NAME"
        FormFieldType.PRINCIPAL_NAME       -> "PRINCIPAL_NAME"
        FormFieldType.SUBJECT              -> "SUBJECT"
        FormFieldType.REASON               -> "REASON"
        FormFieldType.LEAVE_DAYS           -> "LEAVE_DAYS"
        FormFieldType.LEAVE_FROM           -> "LEAVE_FROM"
        FormFieldType.LEAVE_TO             -> "LEAVE_TO"
        FormFieldType.EVENT_NAME           -> "EVENT_NAME"
        FormFieldType.EVENT_DATE           -> "EVENT_DATE"
        FormFieldType.ASSIGNMENT_NAME      -> "ASSIGNMENT_NAME"
        FormFieldType.EXTENSION_DAYS       -> "EXTENSION_DAYS"
        FormFieldType.FEE_TYPE             -> "FEE_TYPE"
        FormFieldType.FEE_AMOUNT           -> "FEE_AMOUNT"
        FormFieldType.DEADLINE_EXTENSION   -> "DEADLINE_EXTENSION"
        FormFieldType.INTERNSHIP_COMPANY   -> "INTERNSHIP_COMPANY"
        FormFieldType.INTERNSHIP_DURATION  -> "INTERNSHIP_DURATION"
        FormFieldType.EXAM_SUBJECT         -> "EXAM_SUBJECT"
        FormFieldType.EXAM_DATE            -> "EXAM_DATE"
        FormFieldType.SCHOLARSHIP_NAME     -> "SCHOLARSHIP_NAME"
        FormFieldType.CUSTOM_NOTE          -> "CUSTOM_NOTE"
    }
}