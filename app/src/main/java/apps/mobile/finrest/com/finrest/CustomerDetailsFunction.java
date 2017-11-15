package apps.mobile.finrest.com.finrest;

/**
 * Created by namrata.s on 21/01/2017.
 */

public class CustomerDetailsFunction {

    String transaction_date,transaction_type,particular,balance,debit_amount,credit_amount,account_number;


    public void setAccount_number(String account_number) {
        this.account_number = account_number;
    }
    public String getAccount_number() {
        return account_number;
    }


    public void setTransaction_date(String transaction_date) {
        this.transaction_date = transaction_date;
    }
    public String getTransaction_date() {
        return transaction_date;
    }


    public void setTransaction_type(String transaction_type) {
        this.transaction_type = transaction_type;
    }
    public String getTransaction_type() {
        return transaction_type;
    }


    public void setBalance(String balance) {
        this.balance = balance;
    }
    public String getBalance() {
        return balance;
    }

    public void setParticular(String particular) {
        this.particular = particular;
    }
    public String getParticular() {
        return particular;
    }

    public void setDebit_amount(String debit_amount) {
        this.debit_amount = debit_amount;
    }
    public String getDebit_amount() {
        return debit_amount;
    }

    public void setCredit_amount(String credit_amount) {
        this.credit_amount = credit_amount;
    }
    public String getCredit_amount() {
        return credit_amount;
    }

}
