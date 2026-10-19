package com.example.nlq;

//public enum OnboardingStep {
//
//    OTP_VERIFICATION(
//            1,
//            "Step 1 - OTP Verification",
//            "step1_otp_verification.md"
//    ),
//
//    PROFILE_DETAILS(
//            2,
//            "Step 2 - Profile Details",
//            "step2_profile_details.md"
//    ),
//
//    ACCOUNT_STATEMENT(
//            3,
//            "Step 3 - Account Statement Upload",
//            "step3_account_statement.md"
//    ),
//
//    UDYAM_DOCUMENT(
//            4,
//            "Step 4 - Udyam Document Upload",
//            "step4_udyam_document.md"
//    );
//
//    private final int stepNumber;
//    private final String displayName;
//    private final String promptFileName;
//
//    OnboardingStep(
//            int stepNumber,
//            String displayName,
//            String promptFileName) {
//        this.stepNumber = stepNumber;
//        this.displayName = displayName;
//        this.promptFileName = promptFileName;
//    }
//
//    public int getStepNumber() {
//        return stepNumber;
//    }
//
//    public String getDisplayName() {
//        return displayName;
//    }
//
//    public String getPromptFileName() {
//        return promptFileName;
//    }
//
//    public static OnboardingStep fromStepNumber(int stepNumber) {
//        for (OnboardingStep step : values()) {
//            if (step.getStepNumber() == stepNumber) {
//                return step;
//            }
//        }
//
//        throw new IllegalArgumentException(
//                "Invalid onboarding step: " + stepNumber
//        );
//    }
//}



public enum OnboardingStep {

    OTP_VERIFICATION(
            1,
            "Step 1 - OTP Verification",
            "otp.md"
    ),

    PROFILE_DETAILS(
            2,
            "Step 2 - Profile Details",
            "profileDetails.md"
    ),

    ACCOUNT_STATEMENT(
            3,
            "Step 3 - Account Statement Upload",
            "accountStatement.md"
    ),

    UDYAM_DOCUMENT(
            4,
            "Step 4 - Udyam Document Upload",
            "udyamDoc.md"
    );

    private final int stepNumber;
    private final String displayName;
    private final String promptFileName;

    OnboardingStep(
            int stepNumber,
            String displayName,
            String promptFileName) {
        this.stepNumber = stepNumber;
        this.displayName = displayName;
        this.promptFileName = promptFileName;
    }

    public int getStepNumber() {
        return stepNumber;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPromptFileName() {
        return promptFileName;
    }

    public static OnboardingStep fromStepNumber(int stepNumber) {
        for (OnboardingStep step : values()) {
            if (step.getStepNumber() == stepNumber) {
                return step;
            }
        }

        throw new IllegalArgumentException(
                "Invalid onboarding step: " + stepNumber
        );
    }
}

