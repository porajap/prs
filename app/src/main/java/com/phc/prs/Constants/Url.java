package com.phc.prs.Constants;

public class Url {
    //check last version
    public static final String GOOGLE_PLAY_VERSION_URL = "https://play.google.com/store/apps/details?id=";

    //url-------------------------------------------------------------------------------------------
    private static final String URL_FOR_TEST_NEW_SERVER = "http://poseintelligence.dyndns.biz:8888/phc_prs_server/";
    private static final String URL_FOR_TEST_OLD_SERVER = "http://poseintelligence.dyndns.biz:8181/phc_prs_server/";
    private static final String URL_FOR_PRODUCTION = "https://phc.dyndns.biz/phc_prs_server/";

    public static final String BASE_URL = URL_FOR_PRODUCTION;

    //login
    public static final String GET_LOIN_URL = "getUserDataLogin.php";

    //check status
    public static final String GET_CHECK_STATUS_ALL_URL = "checkStatus/getCheckStatus.php";

    //dropdown
    public static final String GET_DROPDOWN_URL = "getDropdownData.php";

    //register
    public static final String GET_DROPDOWN_REGISTER_URL = "register/getDropdownRegister.php";
    public static final String SUBMIT_DATA_REGISTER_URL = "register/submitRegister.php";
    public static final String GET_DATA_REGISTER_URL = "register/getDataUserRegister.php";
    public static final String APPROVE_USER_URL = "register/approveUser.php";
    public static final String COUNT_USER_REGISTER_URL = "register/countUserRegister.php";

    //barcode
    public static final String GET_DATA_BARCODE_URL = "barcode/getDataBarcode.php";

    //notification
    public static final String COUNT_NOTIFICATION_URL = "notification/countNotificationRequest.php";
    public static final String GET_DATA_NOTIFICATION_URL = "notification/getDataNotification.php";
    public static final String UPDATE_NOTIFICATION_URL = "notification/updateReadNotification.php";
    public static final String DELETE_NOTIFICATION_URL = "notification/deleteNotification.php";
    public static final String THIS_NOTIFICATION_URL = "checkStatus/getCheckStatus.php";

    //install
    public static final String SEND_INSTALL_URL = "install/sendRequestService.php";

    //question
    public static final String SEND_QUESTION_URL = "question/sendQuestion.php";
    public static final String GET_HISTORY_QUESTION_URL = "question/getHistoryQuestion.php";

    //maintenance
    public static final String SEND_REQUEST_PROBLEM_URL = "maintenance/sendRequestProblem.php";
}
