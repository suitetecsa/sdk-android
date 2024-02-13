package cu.suitetecsa.sdk.android.balance.response;

import java.util.List;

import cu.suitetecsa.sdk.android.balance.consult.UssdRequest;

/**
 * Clase para representar la respuesta de saldo principal
 */
public record PrincipalBalance(double balance, long activeUntil, long dueDate,
                               List<UssdRequest> consults) implements UssdResponse {
}
