package com.fairmontsintenational.rentalapp.utilities;

public class BaseUrl {
    private static String base = "http://197.248.111.170:46/api/v1/";

    public static String PostLogin(){
        return base+"log-in";
    }

    public static String GetLoggedInUser(){
        return base+"user";
    }

    public static String TenantDetails(){
        return base+"tenant";
    }

    public static String PendingBills(){
        return base+"pending-bills";
    }

    public static String Statement(){
        return base+"tenant-statement";
    }

    public static String Transactions(String fromDate,String toDate){
        return base+"approved-transactions?date_from="+fromDate+"&date_to="+toDate;
    }

    public static String GetComplaints(){
        return base+"complaints";
    }

    public static String DeleteComplaint(String slug){
        return base+"complaints/"+slug;
    }

    public static String TerminateLease(String slug){
        return base+"leases/request-termination/"+slug;
    }

    public static String UploadTenantProfilePic(){
        return base+"tenant/profile-image";
    }

    public static String CapturePayment(){
        return base+"payment-captures";
    }

    public static String GetLeaseTerms(){
        return base+"terms-and-conditions";
    }






}
