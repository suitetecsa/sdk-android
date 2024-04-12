SDK Android para SuiteEtecsa
============================

Esta es una librería que facilita la creación de aplicaciones Android dedicadas a gestionar los servicios de [ETECSA](https://www.etecsa.cu).

## Cómo usar

Agrega la dependencia a tu archivo build.gradle/.kts:
[![](https://img.shields.io/maven-central/v/io.github.suitetecsa.sdk/android.svg)](https://img.shields.io/maven-central/v/io.github.suitetecsa.sdk/android.svg)

```kotlin
implementation("io.github.suitetecsa.sdk:android:{última-versión}")
```

### Obtener información de las tarjetas SIM

Para obtener información sobre las tarjetas SIM insertadas en el dispositivo, puedes seguir estos pasos:

#### Kotlin

```kotlin
// Instancia SimCardsAPI
val simCardCollector = SimCardCollector.Builder().build(context)

// Obtiene las tarjetas SIM insertadas en el dispositivo
val simCards = simCardCollector.collect()
```

#### Java

```java
// Instancia SimCardsAPI
SimCardCollector simCardCollector = new SimCardCollector.Builder().build(context);

// Obtiene las tarjetas SIM insertadas en el dispositivo
List<SimCard> simCards = simCardCollector.collect();
```

### Realizar llamadas con una SIM

#### Kotlin

```kotlin
simCards.last().makeCall(context, "51234567")
```

#### Java

```java
SimCardUtils.makeCall(simCards.get(0), context, "51234567");
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
        override fun onRequesting(request: UssdRequest) {
            Toast.makeText(context, "Consultando saldo...", Toast.LENGTH_LONG).show()
        }
        @SuppressLint("MissingPermission")
        override fun onSuccess(request: UssdRequest, ussdResponse: UssdResponse) {
            when (request) {
                UssdRequest.CUSTOM -> {
                    // Convierte el objeto UssdResponse en un objeto MainBalance
                    balance = (ussdResponse as Custom).response.parseMainBalance()
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
final MainBalance balance;

// Enviar la solicitud
SimCardUtils.ussdExecute(firstSimCard, "*222#", new ConsultBalanceCallBack() {
    @Override 
    public void onRequesting(UssdRequest request) {
        Toast.makeText(context, "Consultando saldo...", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSuccess(UssdRequest request, UssdResponse response) {
        if (Objects.requireNonNull(request) == UssdRequest.CUSTOM) {
            balance = MainBalanceParser.parseMainBalance(((Custom) response).response());
        }
    }

    @Override
    public void onFailure(Throwable throwable) {
        throwable.printStackTrace();
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
            override fun onRequesting(resquest: UssdRequest) {
                val consultMessage = when (resquest) {
                    UssdRequest.BONUS_BALANCE -> "Consultando Bonos"
                    UssdRequest.DATA_BALANCE -> "Consultando Datos"
                    UssdRequest.MESSAGES_BALANCE -> "Consultando SMS"
                    UssdRequest.PRINCIPAL_BALANCE -> "Consultando Saldo"
                    UssdRequest.VOICE_BALANCE -> "Consultando Minutos"
                    UssdRequest.CUSTOM -> ""
                }
                Toast.makeText(context, consultMessage, Toast.LENGTH_LONG).show()
            }
            override fun onSuccess(resquest: UssdRequest, ussdResponse: UssdResponse) {
                when (resquest) {
                    UssdRequest.BONUS_BALANCE -> {
                        // Contiene la informacion extraida de la consulta de bono (*222*266#).
                        // Es la ultima operacion en realizarse
                        Toast.makeText(
                            context,
                            "${(ussdResponse as BonusBalance).credit.balance}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    UssdRequest.DATA_BALANCE -> {
                        // Contiene la informacion extraida de la consulta de datos (*222*328#).
                        // Solo se ejecuta si se detecta paquetes de datos en el saldo principal.
                        Toast.makeText(
                            context,
                            "${(ussdResponse as DataBalance).usageBasedPricing}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    UssdRequest.MESSAGES_BALANCE -> {
                        // Contiene la informacion extraida de la consulta de mensajes (*222*767#).
                        // Solo se ejecuta si se detecta paquetes de SMS en el saldo principal.
                        Toast.makeText(
                            context,
                            "${(ussdResponse as MessagesBalance).sms}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    UssdRequest.PRINCIPAL_BALANCE -> {
                        // Contiene la informacion extraida de la consulta de saldo (*222#).
                        // Es la primera operacion en realizarse
                        Toast.makeText(
                            context,
                            "${(ussdResponse as PrincipalBalance).balance}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    UssdRequest.VOICE_BALANCE -> {
                        // Contiene la informacion extraida de la consulta de mensajes (*222*869#).
                        // Solo se ejecuta si se detecta paquetes de Voz en el saldo principal.
                        Toast.makeText(
                            context,
                            "${(ussdResponse as VoiceBalance).seconds}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    UssdRequest.CUSTOM -> {
                        // No se ejecuta en la consulta de saldo automacica.
                    }
                }
            }

            override fun onFailure(throwable: Throwable?) {
                throwable?.let { throw it }
            }
        }
    )
}
```

#### Java

```java
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    SimCardUtils.ussdExecute(firstSimCard, new ConsultBalanceCallBack() {
        @SuppressLint("MissingPermission")
        @Override
        public void onRequesting(UssdRequest request) {
            String consultMessage = switch (request) {
                case BONUS_BALANCE -> "Consultando Bonos...";
                case DATA_BALANCE -> "Consultando Datos...";
                case MESSAGES_BALANCE -> "Consultando SMS...";
                case PRINCIPAL_BALANCE -> "Consultando Saldo...";
                case VOICE_BALANCE -> "Consultando Minutos...";
                default -> "";
            };
            Toast.makeText(context, consultMessage, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onSuccess(UssdRequest request, UssdResponse response) {
            switch (request) {
                case PRINCIPAL_BALANCE ->
                    // Contiene la informacion extraida de la consulta de saldo (*222#).
                    // Es la primera operacion en realizarse
                    Toast.makeText(
                        context,
                        ((PrincipalBalance) response).balance(),
                        Toast.LENGTH_LONG
                    ).show();
                case DATA_BALANCE ->
                    // Contiene la informacion extraida de la consulta de datos (*222*328#).
                    // Solo se ejecuta si se detecta paquetes de datos en el saldo principal.
                    Toast.makeText(
                        context,
                        ((DataBalance) response).usageBasedPricing(),
                        Toast.LENGTH_LONG
                    ).show();
                case VOICE_BALANCE ->
                    // Contiene la informacion extraida de la consulta de mensajes (*222*869#).
                    // Solo se ejecuta si se detecta paquetes de Voz en el saldo principal.
                    Toast.makeText(
                        context,
                        ((VoiceBalance) response).seconds(),
                        Toast.LENGTH_LONG
                    ).show();
                case MESSAGES_BALANCE ->
                    // Contiene la informacion extraida de la consulta de mensajes (*222*767#).
                    // Solo se ejecuta si se detecta paquetes de SMS en el saldo principal.
                    Toast.makeText(
                        context,
                        ((MessagesBalance) response).sms(),
                        Toast.LENGTH_LONG
                    ).show();
                case BONUS_BALANCE ->
                    // Contiene la informacion extraida de la consulta de bono (*222*266#).
                    // Es la ultima operacion en realizarse
                    Toast.makeText(
                        context,
                        ((BonusBalance) response).credit().balance(),
                        Toast.LENGTH_LONG
                    ).show();
                default -> {}
            }
        }

        @Override
        public void onFailure(Throwable throwable) {
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
