SDK Android para SuiteEtecsa
============================

[![](https://jitpack.io/v/suitetecsa/sdk-android.svg)](https://jitpack.io/#suitetecsa/sdk-android)

Esta es una librería que facilita la creación de aplicaciones Android dedicadas a gestionar los servicios de [ETECSA](https://www.etecsa.cu).

## Cómo usar

Agrega la dependencia a tu archivo build.gradle/.kts:

```kotlin
implementation("com.github.suitetecsa.sdk-android:{última-versión}")
```

### Obtener información de las tarjetas SIM

Para obtener información sobre las tarjetas SIM insertadas en el dispositivo, puedes seguir estos pasos:

#### Kotlin

```kotlin
// Instancia SimCardsAPI
val simCardsAPI = SimCardsAPI
    .Builder(context)
    .build()

// Obtiene las tarjetas SIM insertadas en el dispositivo
val simCards = simCardsAPI.getSimCards()
```

#### Java

```java
// Instancia SimCardsAPI
SimCardsAPI simCardsAPI = SimCardsAPI
    .Builder(context)
    .build();

// Obtiene las tarjetas SIM insertadas en el dispositivo
List<SimCard> simCards = simCardsAPI.getSimCards();
```

### Realizar llamadas con una SIM

#### Kotlin

```kotlin
simcCards.last().makeCall(context, "51234567")
```

#### Java

```java
SimCardExtensionKt.makeCall(simCards.get(0), context, "51234567")
```

### Obtener saldo de la tarjeta SIM

Para obtener el saldo de la primera tarjeta SIM de la lista, puedes seguir estos pasos:

#### Kotlin

```kotlin
// Obtener la primera SIM de la lista
val firstSimCard = simCards.first()
var balance: MainBalance? = null
// Enviar la solicitud
firstSimCard.ussdExecute(
    "*222#",
    object : ConsultBalanceCallBack {
        override fun onRequesting(consultType: UssdConsultType) {
            Toast.makeText(context, "Consultando saldo...", Toast.LENGTH_LONG).show()
        }
        override fun onSuccess(ussdResponse: UssdResponse) {
            when (ussdResponse) {
                is UssdResponse.Custom -> {
                    // Convierte el objeto UssdResponse en un objeto MainBalance
                    balance = ussdResponse.response.parseMainBalance()
                }
                else -> {}
            }
        }
        override fun onFailure(throwable: Throwable) {
            throw throwable
        }
    }
)
```

#### Java

```java
// Obtener la primera SIM de la lista
SimCard firstSimCard = simCards.get(0);
MainBalance balance;

// Enviar la solicitud
SimCardExtensionKt.ussdExecute(firstSimCard, "*222#", new ConsultBalanceCallBack() {
    @Override
    public void onRequesting(@NonNull UssdConsultType consultType) {
        Toast.makeText(context, "Consultando saldo...", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSuccess(@NonNull UssdResponse ussdResponse) {
        if (ussdResponse instanceof UssdResponse.Custom) {
            UssdResponse.Custom customResponse = (UssdResponse.Custom) ussdResponse;
            // Convierte el objeto UssdResponse en un objeto MainBalance
            balance = parseMainBalance(customResponse.getResponse());
        }
    }

    @Override
    public void onFailure(@NonNull Throwable throwable) {
        throw throwable;
    }
});
```

### Consultar todo el saldo disponible.

`consultBalance` es una función de extensión que realiza las consultas de saldo automáticamente, consulta primero el saldo inicial, y dependiendo de la información que extraiga consulta los demás saldos. O sea, que si en el saldo inicial no detecta paquetes de datos, no ejecutará la consulta (\*222\*328#). Esto puede ser un inconveniente si desea consultar siempre el estado de la tarifa por consumo, una forma de solucionarlo es comprobar si la linea posee información de planes de datos y si no fuese el caso hacer la consulta del estado de la TPC usando la función `ussdExecute`.

#### Kotlin

```kotlin
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    simCard.consultBalance(
        object : ConsultBalanceCallBack {
            override fun onRequesting(consultType: UssdConsultType) {
                val consultMessage = when (consultType) {
                    UssdConsultType.BonusBalance -> "Consultando Bonos"
                    UssdConsultType.DataBalance -> "Consultando Datos"
                    UssdConsultType.MessagesBalance -> "Consultando SMS"
                    UssdConsultType.PrincipalBalance -> "Consultando Saldo"
                    UssdConsultType.VoiceBalance -> "Consultando Minutos"
                    is UssdConsultType.Custom -> ""
                }
                Toast.makeText(context, consultMessage, Toast.LENGTH_LONG).show()
            }
            override fun onSuccess(ussdResponse: UssdResponse) {
                when (ussdResponse) {
                    is UssdResponse.BonusBalance -> {
                        // Contiene la informacion extraida de la consulta de bono (*222*266#).
                        // Es la ultima operacion en realizarse
                        Toast.makeText(
                            context,
                            "${ussdResponse.credit}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    is UssdResponse.DataBalance -> {
                        // Contiene la informacion extraida de la consulta de datos (*222*328#).
                        // Solo se ejecuta si se detecta paquetes de datos en el saldo principal.
                        Toast.makeText(
                            context,
                            "${ussdResponse.usageBasedPricing}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    is UssdResponse.MessagesBalance -> {
                        // Contiene la informacion extraida de la consulta de mensajes (*222*767#).
                        // Solo se ejecuta si se detecta paquetes de SMS en el saldo principal.
                        Toast.makeText(
                            context,
                            "${ussdResponse.count}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    is UssdResponse.PrincipalBalance -> {
                        // Contiene la informacion extraida de la consulta de saldo (*222#).
                        // Es la primera operacion en realizarse
                        Toast.makeText(
                            context,
                            "${ussdResponse.credit}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    is UssdResponse.VoiceBalance -> {
                        // Contiene la informacion extraida de la consulta de mensajes (*222*869#).
                        // Solo se ejecuta si se detecta paquetes de Voz en el saldo principal.
                        Toast.makeText(
                            context,
                            "${ussdResponse.count}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    is UssdResponse.Custom -> {
                        // No se ejecuta en la consulta de saldo automacica.
                    }
                }
            }
        }
    )
}
```

#### Java

```java
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    SimCardExtensionKt.ussdExecute(firstSimCard, new ConsultBalanceCallBack() {
        @SuppressLint("MissingPermission")
        @Override
        public void onRequesting(@NonNull UssdConsultType consultType) {
            String consultMessage;
            if (consultType instanceof UssdConsultType.BonusBalance) {
                consultMessage = "Consultando Bonos...";
            } else if (consultType instanceof UssdConsultType.DataBalance) {
                consultMessage = "Consultando Datos...";
            } else if (consultType instanceof UssdConsultType.MessagesBalance) {
                consultMessage = "Consultando SMS...";
            } else if (consultType instanceof UssdConsultType.PrincipalBalance) {
                consultMessage = "Consultando Saldo...";
            } else if (consultType instanceof UssdConsultType.VoiceBalance) {
                consultMessage = "Consultando Minutos...";
            } else {
                consultMessage = "";
            }
            Toast.makeText(context, consultMessage, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onSuccess(@NonNull UssdResponse ussdResponse) {
            if (ussdResponse instanceof UssdResponse.BonusBalance) {
                // Contiene la informacion extraida de la consulta de bono (*222*266#).
                // Es la ultima operacion en realizarse
                Toast.makeText(
                    context,
                    ((UssdResponse.BonusBalance) ussdResponse).getCredit(),
                    Toast.LENGTH_LONG
                ).show();
            } else if (ussdResponse instanceof UssdResponse.DataBalance) {
                // Contiene la informacion extraida de la consulta de datos (*222*328#).
                // Solo se ejecuta si se detecta paquetes de datos en el saldo principal.
                Toast.makeText(
                    context,
                    ((UssdResponse.DataBalance) ussdResponse).getUsageBasedPricing(),
                    Toast.LENGTH_LONG
                ).show();
            } else if (ussdResponse instanceof UssdResponse.MessagesBalance) {
                // Contiene la informacion extraida de la consulta de mensajes (*222*767#).
                // Solo se ejecuta si se detecta paquetes de SMS en el saldo principal.
                Toast.makeText(
                    context,
                    ((UssdResponse.MessagesBalance) ussdResponse).getCount(),
                    Toast.LENGTH_LONG
                ).show();
            } else if (ussdResponse instanceof UssdResponse.PrincipalBalance) {
                // Contiene la informacion extraida de la consulta de saldo (*222#).
                // Es la primera operacion en realizarse
                Toast.makeText(
                    context,
                    ((UssdResponse.PrincipalBalance) ussdResponse).getCredit(),
                    Toast.LENGTH_LONG
                ).show();
            } else if (ussdResponse instanceof UssdResponse.VoiceBalance) {
                // Contiene la informacion extraida de la consulta de mensajes (*222*869#).
                // Solo se ejecuta si se detecta paquetes de Voz en el saldo principal.
                Toast.makeText(
                    context,
                    ((UssdResponse.VoiceBalance) ussdResponse).getTime(),
                    Toast.LENGTH_LONG
                ).show();
            }
        }

        @Override
        public void onFailure(@NonNull Throwable throwable) {
            throwable.printStackTrace();
        }
    });
}
```

# Contribución

¡Gracias por tu interés en colaborar con nuestro proyecto! Nos encanta recibir contribuciones de la
comunidad y valoramos mucho tu tiempo y esfuerzo.

## Cómo contribuir

Si estás interesado en contribuir, por favor sigue los siguientes pasos:

1. Revisa las issues abiertas para ver si hay alguna tarea en la que puedas ayudar.
2. Si no encuentras ninguna issue que te interese, por favor abre una nueva issue explicando el problema o la funcionalidad que te gustaría implementar. Asegúrate de incluir toda la información necesaria para que otros puedan entender el problema o la funcionalidad que estás proponiendo.
3. Si ya tienes una issue asignada o si has decidido trabajar en una tarea existente, por favor crea un fork del repositorio y trabaja en una nueva rama (`git checkout -b nombre-de-mi-rama`).
4. Cuando hayas terminado de trabajar en la tarea, crea un pull request explicando los cambios que has realizado y asegurándote de que el código cumple con nuestras directrices de estilo y calidad.
5. Espera a que uno de nuestros colaboradores revise el pull request y lo apruebe o sugiera cambios adicionales.

## Directrices de contribución

Por favor, asegúrate de seguir nuestras directrices de contribución para que podamos revisar y aprobar tus cambios de manera efectiva:

- Sigue los estándares de codificación y estilo de nuestro proyecto.
- Asegúrate de que el código nuevo esté cubierto por pruebas unitarias.
- Documenta cualquier cambio que hagas en la documentación del proyecto.

¡Gracias de nuevo por tu interés en contribuir! Si tienes alguna pregunta o necesitas ayuda, no dudes en ponerte en contacto con nosotros en la sección de issues o enviándonos un mensaje directo.

## Licencia

Este proyecto está licenciado bajo la Licencia MIT. Esto significa que tienes permiso para utilizar, copiar, modificar, fusionar, publicar, distribuir, sublicenciar y/o vender copias del software, y para permitir que las personas a las que se les proporcione el software lo hagan, con sujeción a las siguientes condiciones:

- Se debe incluir una copia de la licencia en todas las copias o partes sustanciales del software.
- El software se proporciona "tal cual", sin garantía de ningún tipo, expresa o implícita, incluyendo pero no limitado a garantías de comerciabilidad, aptitud para un propósito particular y no infracción. En ningún caso los autores o titulares de la licencia serán responsables de cualquier reclamo, daño u otra responsabilidad, ya sea en una acción de contrato, agravio o de otra manera, que surja de, fuera de o en conexión con el software o el uso u otros tratos en el software.

Puedes encontrar una copia completa de la Licencia MIT en el archivo LICENSE que se incluye en este repositorio.

## Contacto

Si tienes alguna pregunta o comentario sobre el proyecto, no dudes en ponerte en contacto conmigo a través de los siguientes medios:

- Correo electrónico: [lesclaz95@gmail.com](mailto:lesclaz95@gmail.com)
- Twitter: [@lesclaz](https://twitter.com/lesclaz)
- Telegram: [@lesclaz](https://t.me/lesclaz)

Estaré encantado de escuchar tus comentarios y responder tus preguntas. ¡Gracias por tu interés en mi proyecto!
