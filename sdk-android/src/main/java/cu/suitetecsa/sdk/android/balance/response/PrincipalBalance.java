package cu.suitetecsa.sdk.android.balance.response;

import java.util.List;

import cu.suitetecsa.sdk.android.balance.consult.UssdRequest;

/**
 * Clase para representar la respuesta de saldo principal
 */
public class PrincipalBalance implements UssdResponse {
    private final double balance;
    private final long activeUntil;
    private final long dueDate;
    private final List<UssdRequest> consults;

    public PrincipalBalance(double balance, long activeUntil, long dueDate, List<UssdRequest> consults) {
        this.balance = balance;
        this.activeUntil = activeUntil;
        this.dueDate = dueDate;
        this.consults = consults;
    }

    public double getBalance() {
        return balance;
    }

    public long getActiveUntil() {
        return activeUntil;
    }

    public long getDueDate() {
        return dueDate;
    }

    public List<UssdRequest> getConsults() {
        return consults;
    }
}
