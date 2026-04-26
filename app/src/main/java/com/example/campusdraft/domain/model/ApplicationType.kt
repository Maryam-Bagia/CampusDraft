package com.example.campusdraft.domain.model

/**
 * Represents every type of formal application a student can generate.
 * Each type carries its own icon, category, required fields, and body template.
 */
data class ApplicationType(
    val id: String,
    val title: String,
    val description: String,
    val category: AppCategory,
    val iconName: String,         // Material icon name reference key
    val requiredFields: List<FormFieldType>,
    val template: String          // Mustache-style {{PLACEHOLDER}} template
)

/** High-level categories shown on the home screen filter chips */
enum class AppCategory(val displayName: String) {
    LEAVE("Leave"),
    ACADEMIC("Academic"),
    EVENT("Event"),
    ADMINISTRATIVE("Administrative"),
    FINANCIAL("Financial"),
    LIBRARY("Library")
}

/** Every possible form field across all templates */
enum class FormFieldType(
    val label: String,
    val placeholder: String,
    val isMultiLine: Boolean = false,
    val isOptional: Boolean = false
) {
    STUDENT_NAME("Student Name", "e.g. Rahul Mehta"),
    ENROLLMENT_NUMBER("Enrollment Number", "e.g. EN21CS045"),
    CLASS_SEMESTER("Class / Semester", "e.g. 3rd Semester, CE-B"),
    DEPARTMENT("Department", "e.g. Computer Engineering"),
    DATE("Date", "e.g. 24 April 2026"),
    TEACHER_NAME("Teacher / HOD Name", "e.g. Prof. Anita Sharma"),
    PRINCIPAL_NAME("Principal Name", "e.g. Dr. R. K. Patel"),
    SUBJECT("Subject", "e.g. Data Structures"),
    REASON("Reason", "Describe your reason clearly", isMultiLine = true),
    LEAVE_DAYS("Number of Days", "e.g. 3"),
    LEAVE_FROM("Leave From Date", "e.g. 25 April 2026"),
    LEAVE_TO("Leave To Date", "e.g. 27 April 2026"),
    EVENT_NAME("Event Name", "e.g. Technovation 2026"),
    EVENT_DATE("Event Date", "e.g. 30 April 2026"),
    ASSIGNMENT_NAME("Assignment Name", "e.g. Unit-3 Assignment"),
    EXTENSION_DAYS("Extension Days Needed", "e.g. 5"),
    FEE_TYPE("Fee Type", "e.g. Tuition Fee, Exam Fee"),
    FEE_AMOUNT("Fee Amount (₹)", "e.g. ₹8,500"),
    DEADLINE_EXTENSION("New Deadline Requested", "e.g. 10 May 2026"),
    INTERNSHIP_COMPANY("Company Name", "e.g. Infosys Ltd."),
    INTERNSHIP_DURATION("Internship Duration", "e.g. 2 months (May–June 2026)"),
    EXAM_SUBJECT("Exam Subject", "e.g. Operating Systems (CS301)"),
    EXAM_DATE("Exam Date", "e.g. 15 March 2026"),
    SCHOLARSHIP_NAME("Scholarship Name", "e.g. State Merit Scholarship"),
    CUSTOM_NOTE("Additional Note (optional)", "Any extra information", isMultiLine = true, isOptional = true)
}

/** In-memory singleton list of all supported application types */
object ApplicationTypes {

    val all: List<ApplicationType> = listOf(

        // ─── LEAVE ──────────────────────────────────────────────────────────────

        ApplicationType(
            id = "leave_general",
            title = "Leave Application",
            description = "Request casual or general leave from college",
            category = AppCategory.LEAVE,
            iconName = "calendar_today",
            requiredFields = listOf(
                FormFieldType.DATE, FormFieldType.TEACHER_NAME,
                FormFieldType.STUDENT_NAME, FormFieldType.ENROLLMENT_NUMBER,
                FormFieldType.CLASS_SEMESTER, FormFieldType.DEPARTMENT,
                FormFieldType.LEAVE_FROM, FormFieldType.LEAVE_TO,
                FormFieldType.LEAVE_DAYS, FormFieldType.REASON,
                FormFieldType.CUSTOM_NOTE
            ),
            template = """
Date: {{DATE}}

To,
{{TEACHER_NAME}}
Department of {{DEPARTMENT}}
[College Name]

Subject: Application for Leave

Respected Sir/Madam,

I, {{STUDENT_NAME}}, a student of {{CLASS_SEMESTER}}, Department of {{DEPARTMENT}}, with Enrollment Number {{ENROLLMENT_NUMBER}}, humbly request you to grant me leave for {{LEAVE_DAYS}} day(s), from {{LEAVE_FROM}} to {{LEAVE_TO}}.

The reason for my leave is as follows:
{{REASON}}

{{CUSTOM_NOTE}}

I assure you that I will complete all pending academic work upon my return and shall not let this absence affect my studies.

Thanking you in anticipation.

Yours obediently,
{{STUDENT_NAME}}
Enrollment No.: {{ENROLLMENT_NUMBER}}
Class: {{CLASS_SEMESTER}}
Department: {{DEPARTMENT}}
            """.trimIndent()
        ),

        ApplicationType(
            id = "leave_sick",
            title = "Sick Leave Application",
            description = "Apply for medical / sick leave with doctor's note reference",
            category = AppCategory.LEAVE,
            iconName = "local_hospital",
            requiredFields = listOf(
                FormFieldType.DATE, FormFieldType.TEACHER_NAME,
                FormFieldType.STUDENT_NAME, FormFieldType.ENROLLMENT_NUMBER,
                FormFieldType.CLASS_SEMESTER, FormFieldType.DEPARTMENT,
                FormFieldType.LEAVE_FROM, FormFieldType.LEAVE_TO,
                FormFieldType.LEAVE_DAYS, FormFieldType.REASON,
                FormFieldType.CUSTOM_NOTE
            ),
            template = """
Date: {{DATE}}

To,
{{TEACHER_NAME}}
Department of {{DEPARTMENT}}
[College Name]

Subject: Application for Sick Leave

Respected Sir/Madam,

I, {{STUDENT_NAME}}, Enrollment No. {{ENROLLMENT_NUMBER}}, a student of {{CLASS_SEMESTER}}, Department of {{DEPARTMENT}}, wish to inform you that I am suffering from {{REASON}} and am unable to attend college from {{LEAVE_FROM}} to {{LEAVE_TO}} ({{LEAVE_DAYS}} day(s)).

I am currently under medical supervision and will submit the doctor's certificate upon rejoining college. I kindly request you to grant me medical leave for the aforementioned period.

{{CUSTOM_NOTE}}

I sincerely apologise for any inconvenience caused and assure you that I will cover all missed coursework diligently.

Thanking you,

Yours obediently,
{{STUDENT_NAME}}
Enrollment No.: {{ENROLLMENT_NUMBER}}
Class: {{CLASS_SEMESTER}}
Department: {{DEPARTMENT}}
            """.trimIndent()
        ),

        // ─── ACADEMIC ───────────────────────────────────────────────────────────

        ApplicationType(
            id = "assignment_delay",
            title = "Assignment Delay Request",
            description = "Request extra time to submit an assignment",
            category = AppCategory.ACADEMIC,
            iconName = "assignment_late",
            requiredFields = listOf(
                FormFieldType.DATE, FormFieldType.TEACHER_NAME,
                FormFieldType.STUDENT_NAME, FormFieldType.ENROLLMENT_NUMBER,
                FormFieldType.CLASS_SEMESTER, FormFieldType.DEPARTMENT,
                FormFieldType.SUBJECT, FormFieldType.ASSIGNMENT_NAME,
                FormFieldType.EXTENSION_DAYS, FormFieldType.DEADLINE_EXTENSION,
                FormFieldType.REASON, FormFieldType.CUSTOM_NOTE
            ),
            template = """
Date: {{DATE}}

To,
{{TEACHER_NAME}}
Subject: {{SUBJECT}}
Department of {{DEPARTMENT}}
[College Name]

Subject: Request for Assignment Submission Extension

Respected Sir/Madam,

I, {{STUDENT_NAME}}, Enrollment No. {{ENROLLMENT_NUMBER}}, student of {{CLASS_SEMESTER}}, Department of {{DEPARTMENT}}, respectfully wish to bring to your attention that I am unable to submit the {{ASSIGNMENT_NAME}} by the prescribed deadline due to the following reason:

{{REASON}}

I humbly request you to grant me an extension of {{EXTENSION_DAYS}} additional day(s) and allow me to submit the assignment by {{DEADLINE_EXTENSION}}.

{{CUSTOM_NOTE}}

I assure you that the assignment will be completed thoroughly and submitted on or before the requested date.

Thanking you for your kind consideration.

Yours sincerely,
{{STUDENT_NAME}}
Enrollment No.: {{ENROLLMENT_NUMBER}}
Class: {{CLASS_SEMESTER}}
Department: {{DEPARTMENT}}
            """.trimIndent()
        ),

        ApplicationType(
            id = "early_submission",
            title = "Early Submission Request",
            description = "Request to submit an assignment before the due date",
            category = AppCategory.ACADEMIC,
            iconName = "assignment_turned_in",
            requiredFields = listOf(
                FormFieldType.DATE, FormFieldType.TEACHER_NAME,
                FormFieldType.STUDENT_NAME, FormFieldType.ENROLLMENT_NUMBER,
                FormFieldType.CLASS_SEMESTER, FormFieldType.DEPARTMENT,
                FormFieldType.SUBJECT, FormFieldType.ASSIGNMENT_NAME,
                FormFieldType.REASON, FormFieldType.CUSTOM_NOTE
            ),
            template = """
Date: {{DATE}}

To,
{{TEACHER_NAME}}
Subject: {{SUBJECT}}
Department of {{DEPARTMENT}}
[College Name]

Subject: Request for Early Submission of Assignment

Respected Sir/Madam,

I, {{STUDENT_NAME}}, Enrollment No. {{ENROLLMENT_NUMBER}}, student of {{CLASS_SEMESTER}}, Department of {{DEPARTMENT}}, would like to request your permission to submit the {{ASSIGNMENT_NAME}} for {{SUBJECT}} before the stipulated deadline.

The reason for this early submission request is:
{{REASON}}

{{CUSTOM_NOTE}}

I assure you that the assignment has been prepared diligently and meets all the required standards. I would be grateful if you kindly accept my early submission.

Thanking you,

Yours sincerely,
{{STUDENT_NAME}}
Enrollment No.: {{ENROLLMENT_NUMBER}}
Class: {{CLASS_SEMESTER}}
Department: {{DEPARTMENT}}
            """.trimIndent()
        ),

        ApplicationType(
            id = "exam_rechecking",
            title = "Exam Rechecking Request",
            description = "Request re-evaluation of your answer sheet",
            category = AppCategory.ACADEMIC,
            iconName = "rate_review",
            requiredFields = listOf(
                FormFieldType.DATE, FormFieldType.PRINCIPAL_NAME,
                FormFieldType.STUDENT_NAME, FormFieldType.ENROLLMENT_NUMBER,
                FormFieldType.CLASS_SEMESTER, FormFieldType.DEPARTMENT,
                FormFieldType.EXAM_SUBJECT, FormFieldType.EXAM_DATE,
                FormFieldType.REASON, FormFieldType.CUSTOM_NOTE
            ),
            template = """
Date: {{DATE}}

To,
{{PRINCIPAL_NAME}}
[College Name]

Subject: Request for Re-evaluation / Rechecking of Answer Sheet

Respected Sir/Madam,

I, {{STUDENT_NAME}}, Enrollment No. {{ENROLLMENT_NUMBER}}, student of {{CLASS_SEMESTER}}, Department of {{DEPARTMENT}}, am writing this application to kindly request the re-evaluation of my answer sheet for the subject {{EXAM_SUBJECT}}, examination held on {{EXAM_DATE}}.

The reason for this request is:
{{REASON}}

{{CUSTOM_NOTE}}

I believe there may be an error in marking and respectfully request that the answer sheet be reviewed by the concerned faculty. I am ready to pay the applicable rechecking fee as per college norms.

Thanking you in anticipation of a favourable response.

Yours obediently,
{{STUDENT_NAME}}
Enrollment No.: {{ENROLLMENT_NUMBER}}
Class: {{CLASS_SEMESTER}}
Department: {{DEPARTMENT}}
            """.trimIndent()
        ),

        // ─── EVENT ──────────────────────────────────────────────────────────────

        ApplicationType(
            id = "teachers_day",
            title = "Teachers' Day Permission",
            description = "Request permission to organise Teachers' Day celebration",
            category = AppCategory.EVENT,
            iconName = "celebration",
            requiredFields = listOf(
                FormFieldType.DATE, FormFieldType.PRINCIPAL_NAME,
                FormFieldType.STUDENT_NAME, FormFieldType.ENROLLMENT_NUMBER,
                FormFieldType.CLASS_SEMESTER, FormFieldType.DEPARTMENT,
                FormFieldType.EVENT_DATE, FormFieldType.REASON,
                FormFieldType.CUSTOM_NOTE
            ),
            template = """
Date: {{DATE}}

To,
{{PRINCIPAL_NAME}}
[College Name]

Subject: Request for Permission to Organise Teachers' Day Celebration

Respected Sir/Madam,

We, the students of {{CLASS_SEMESTER}}, Department of {{DEPARTMENT}}, wish to celebrate Teachers' Day on {{EVENT_DATE}} as a token of love, respect, and gratitude towards our esteemed faculty.

We are writing to seek your kind permission to organise a small cultural programme and felicitation ceremony on the said date. The event will be conducted with complete discipline and will not disrupt regular academic activities.

Proposed plan:
{{REASON}}

{{CUSTOM_NOTE}}

We assure you that all arrangements will be made responsibly and the event will reflect the values and traditions of our college.

Thanking you,

Yours obediently,
{{STUDENT_NAME}} (Class Representative)
Enrollment No.: {{ENROLLMENT_NUMBER}}
Class: {{CLASS_SEMESTER}}
Department: {{DEPARTMENT}}
            """.trimIndent()
        ),

        ApplicationType(
            id = "tech_fest",
            title = "Tech Fest Participation",
            description = "Request permission to participate in a technical festival",
            category = AppCategory.EVENT,
            iconName = "devices",
            requiredFields = listOf(
                FormFieldType.DATE, FormFieldType.PRINCIPAL_NAME,
                FormFieldType.STUDENT_NAME, FormFieldType.ENROLLMENT_NUMBER,
                FormFieldType.CLASS_SEMESTER, FormFieldType.DEPARTMENT,
                FormFieldType.EVENT_NAME, FormFieldType.EVENT_DATE,
                FormFieldType.REASON, FormFieldType.CUSTOM_NOTE
            ),
            template = """
Date: {{DATE}}

To,
{{PRINCIPAL_NAME}}
[College Name]

Subject: Request for Permission to Participate in {{EVENT_NAME}}

Respected Sir/Madam,

I, {{STUDENT_NAME}}, Enrollment No. {{ENROLLMENT_NUMBER}}, student of {{CLASS_SEMESTER}}, Department of {{DEPARTMENT}}, respectfully request your permission to participate in {{EVENT_NAME}} scheduled on {{EVENT_DATE}}.

Details of the event:
{{REASON}}

{{CUSTOM_NOTE}}

Participating in this technical festival will provide me with a valuable opportunity to apply my theoretical knowledge, enhance practical skills, and represent our institution at an inter-college platform.

I assure you that I will manage my academic responsibilities and make up for any missed coursework diligently.

Thanking you in anticipation.

Yours obediently,
{{STUDENT_NAME}}
Enrollment No.: {{ENROLLMENT_NUMBER}}
Class: {{CLASS_SEMESTER}}
Department: {{DEPARTMENT}}
            """.trimIndent()
        ),

        // ─── ADMINISTRATIVE ─────────────────────────────────────────────────────

        ApplicationType(
            id = "bonafide_certificate",
            title = "Bonafide Certificate Request",
            description = "Request a bonafide certificate for various purposes",
            category = AppCategory.ADMINISTRATIVE,
            iconName = "verified",
            requiredFields = listOf(
                FormFieldType.DATE, FormFieldType.PRINCIPAL_NAME,
                FormFieldType.STUDENT_NAME, FormFieldType.ENROLLMENT_NUMBER,
                FormFieldType.CLASS_SEMESTER, FormFieldType.DEPARTMENT,
                FormFieldType.REASON, FormFieldType.CUSTOM_NOTE
            ),
            template = """
Date: {{DATE}}

To,
{{PRINCIPAL_NAME}}
[College Name]

Subject: Request for Bonafide Certificate

Respected Sir/Madam,

I, {{STUDENT_NAME}}, Enrollment No. {{ENROLLMENT_NUMBER}}, am currently enrolled as a bonafide student of {{CLASS_SEMESTER}}, Department of {{DEPARTMENT}} at [College Name].

I require a Bonafide Certificate for the following purpose:
{{REASON}}

{{CUSTOM_NOTE}}

I kindly request you to issue the Bonafide Certificate at the earliest, as it is urgently needed for the above-mentioned purpose.

Thanking you,

Yours faithfully,
{{STUDENT_NAME}}
Enrollment No.: {{ENROLLMENT_NUMBER}}
Class: {{CLASS_SEMESTER}}
Department: {{DEPARTMENT}}
Contact: [Your Phone Number]
            """.trimIndent()
        ),

        ApplicationType(
            id = "internship_permission",
            title = "Internship Permission Letter",
            description = "Request permission to undertake an industrial internship",
            category = AppCategory.ADMINISTRATIVE,
            iconName = "work",
            requiredFields = listOf(
                FormFieldType.DATE, FormFieldType.PRINCIPAL_NAME,
                FormFieldType.STUDENT_NAME, FormFieldType.ENROLLMENT_NUMBER,
                FormFieldType.CLASS_SEMESTER, FormFieldType.DEPARTMENT,
                FormFieldType.INTERNSHIP_COMPANY, FormFieldType.INTERNSHIP_DURATION,
                FormFieldType.REASON, FormFieldType.CUSTOM_NOTE
            ),
            template = """
Date: {{DATE}}

To,
{{PRINCIPAL_NAME}}
[College Name]

Subject: Request for Permission to Undertake Internship

Respected Sir/Madam,

I, {{STUDENT_NAME}}, Enrollment No. {{ENROLLMENT_NUMBER}}, student of {{CLASS_SEMESTER}}, Department of {{DEPARTMENT}}, am pleased to inform you that I have been selected for an internship at {{INTERNSHIP_COMPANY}} for a duration of {{INTERNSHIP_DURATION}}.

I respectfully request your permission and, if applicable, a recommendation/permission letter from the institution to join this internship.

Purpose / Role:
{{REASON}}

{{CUSTOM_NOTE}}

I assure you that undertaking this internship will enhance my industry knowledge and technical skills, which will complement my academic learning. I will ensure that all academic obligations are duly fulfilled.

Thanking you for your kind consideration.

Yours obediently,
{{STUDENT_NAME}}
Enrollment No.: {{ENROLLMENT_NUMBER}}
Class: {{CLASS_SEMESTER}}
Department: {{DEPARTMENT}}
            """.trimIndent()
        ),

        // ─── FINANCIAL ──────────────────────────────────────────────────────────

        ApplicationType(
            id = "fee_extension",
            title = "Fee Extension Request",
            description = "Request an extension for fee payment deadline",
            category = AppCategory.FINANCIAL,
            iconName = "payment",
            requiredFields = listOf(
                FormFieldType.DATE, FormFieldType.PRINCIPAL_NAME,
                FormFieldType.STUDENT_NAME, FormFieldType.ENROLLMENT_NUMBER,
                FormFieldType.CLASS_SEMESTER, FormFieldType.DEPARTMENT,
                FormFieldType.FEE_TYPE, FormFieldType.FEE_AMOUNT,
                FormFieldType.DEADLINE_EXTENSION, FormFieldType.REASON,
                FormFieldType.CUSTOM_NOTE
            ),
            template = """
Date: {{DATE}}

To,
{{PRINCIPAL_NAME}}
[College Name]

Subject: Request for Extension of Fee Payment Deadline

Respected Sir/Madam,

I, {{STUDENT_NAME}}, Enrollment No. {{ENROLLMENT_NUMBER}}, student of {{CLASS_SEMESTER}}, Department of {{DEPARTMENT}}, respectfully wish to bring to your kind notice that I am currently facing financial difficulties and am unable to pay the {{FEE_TYPE}} of {{FEE_AMOUNT}} by the prescribed deadline.

Reason for financial difficulty:
{{REASON}}

{{CUSTOM_NOTE}}

I humbly request you to grant me an extension until {{DEADLINE_EXTENSION}} to arrange the fee amount. I assure you that the payment will be made before the extended deadline without fail.

I sincerely apologise for any inconvenience and thank you for your understanding and support.

Yours faithfully,
{{STUDENT_NAME}}
Enrollment No.: {{ENROLLMENT_NUMBER}}
Class: {{CLASS_SEMESTER}}
Department: {{DEPARTMENT}}
            """.trimIndent()
        ),

        ApplicationType(
            id = "scholarship_request",
            title = "Scholarship Request",
            description = "Apply for a merit or need-based scholarship",
            category = AppCategory.FINANCIAL,
            iconName = "school",
            requiredFields = listOf(
                FormFieldType.DATE, FormFieldType.PRINCIPAL_NAME,
                FormFieldType.STUDENT_NAME, FormFieldType.ENROLLMENT_NUMBER,
                FormFieldType.CLASS_SEMESTER, FormFieldType.DEPARTMENT,
                FormFieldType.SCHOLARSHIP_NAME, FormFieldType.REASON,
                FormFieldType.CUSTOM_NOTE
            ),
            template = """
Date: {{DATE}}

To,
{{PRINCIPAL_NAME}}
[College Name]

Subject: Application for {{SCHOLARSHIP_NAME}}

Respected Sir/Madam,

I, {{STUDENT_NAME}}, Enrollment No. {{ENROLLMENT_NUMBER}}, student of {{CLASS_SEMESTER}}, Department of {{DEPARTMENT}}, am writing to apply for the {{SCHOLARSHIP_NAME}} offered by [Institution/Organisation Name].

I am a sincere and dedicated student and believe that this scholarship will greatly support my academic journey. The details supporting my application are as follows:

{{REASON}}

{{CUSTOM_NOTE}}

I have attached/will provide all necessary documents including mark sheets, income certificate, and any other required papers as directed. I sincerely request you to consider my application favourably and recommend me for this scholarship.

Thanking you in anticipation.

Yours faithfully,
{{STUDENT_NAME}}
Enrollment No.: {{ENROLLMENT_NUMBER}}
Class: {{CLASS_SEMESTER}}
Department: {{DEPARTMENT}}
            """.trimIndent()
        ),

        // ─── LIBRARY ────────────────────────────────────────────────────────────

        ApplicationType(
            id = "library_permission",
            title = "Library Permission / Card Request",
            description = "Request library card or special reading room access",
            category = AppCategory.LIBRARY,
            iconName = "local_library",
            requiredFields = listOf(
                FormFieldType.DATE, FormFieldType.TEACHER_NAME,
                FormFieldType.STUDENT_NAME, FormFieldType.ENROLLMENT_NUMBER,
                FormFieldType.CLASS_SEMESTER, FormFieldType.DEPARTMENT,
                FormFieldType.REASON, FormFieldType.CUSTOM_NOTE
            ),
            template = """
Date: {{DATE}}

To,
{{TEACHER_NAME}} / The Librarian
[College Name]

Subject: Request for Library Card / Permission to Access Library Resources

Respected Sir/Madam,

I, {{STUDENT_NAME}}, Enrollment No. {{ENROLLMENT_NUMBER}}, student of {{CLASS_SEMESTER}}, Department of {{DEPARTMENT}}, respectfully wish to request a library card and/or special access to library resources and the reading room.

Purpose / Requirement:
{{REASON}}

{{CUSTOM_NOTE}}

I assure you that I will abide by all library rules and regulations, handle all books and study materials with care, and return them within the stipulated time period.

Kindly consider my request and grant me the required access.

Thanking you,

Yours sincerely,
{{STUDENT_NAME}}
Enrollment No.: {{ENROLLMENT_NUMBER}}
Class: {{CLASS_SEMESTER}}
Department: {{DEPARTMENT}}
            """.trimIndent()
        )
    )
}