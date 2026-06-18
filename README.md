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

### Realizar consultas USSD con una SIM

#### Kotlin

```kotlin
simCards.last().ussdFetch("*222#", object : UssdStringCallback {
    override fun onSuccess(rawResponse: String) { }
    override fun onFailure(throwable: Throwable) { }
})
```

#### Java

```java
SimCardUtils.ussdFetch(simCards.get(0), "*222#", new UssdStringCallback() {
    @Override
    public void onSuccess(String rawResponse) { }

    @Override
    public void onFailure(Throwable throwable) { }
});
```

### Consultas tipadas (recomendado)

El SDK ofrece métodos semánticos que combinan el envío USSD + parseo en una sola llamada,
usando `SdkCallback<T>` para devolver el modelo ya tipado:

#### Kotlin

```kotlin
val sim = simCards.first()

sim.queryBalance(object : SdkCallback<MainBalance> {
    override fun onSuccess(balance: MainBalance) {
        // balance.balance: Float
        // balance.data: Long? (bytes)
        // balance.voice: Long? (segundos)
        // balance.sms: Int?
        // balance.lockDate: Date
        // balance.deletionDate: Date
    }
    override fun onFailure(error: Throwable) { }
})

sim.queryData(object : SdkCallback<MainData> {
    override fun onSuccess(data: MainData) { }
    override fun onFailure(error: Throwable) { }
})

sim.queryVoice(object : SdkCallback<VoiceBalance> {
    override fun onSuccess(voice: VoiceBalance) { }
    override fun onFailure(error: Throwable) { }
})

sim.querySms(object : SdkCallback<MessagesBalance> {
    override fun onSuccess(sms: MessagesBalance) { }
    override fun onFailure(error: Throwable) { }
})

sim.queryBonuses(object : SdkCallback<BonusBalance> {
    override fun onSuccess(bonus: BonusBalance) { }
    override fun onFailure(error: Throwable) { }
})
```

#### Java

```java
SimCard sim = simCards.get(0);

SimCardBalanceQueries.queryBalance(sim, new SdkCallback<MainBalance>() {
    @Override
    public void onSuccess(MainBalance balance) { }
    @Override
    public void onFailure(Throwable error) { }
});

SimCardBalanceQueries.queryData(sim, new SdkCallback<MainData>() { ... });
SimCardBalanceQueries.queryVoice(sim, new SdkCallback<VoiceBalance>() { ... });
SimCardBalanceQueries.querySms(sim, new SdkCallback<MessagesBalance>() { ... });
SimCardBalanceQueries.queryBonuses(sim, new SdkCallback<BonusBalance>() { ... });
```

| Método | Código USSD | Devuelve |
|---|---|---|
| `queryBalance(callback)` | `*222#` | `MainBalance` |
| `queryData(callback)` | `*222*328#` | `MainData` |
| `queryVoice(callback)` | `*222*869#` | `VoiceBalance` |
| `querySms(callback)` | `*222*767#` | `MessagesBalance` |
| `queryBonuses(callback)` | `*222*266#` | `BonusBalance` |

### API de bajo nivel

Si necesitas enviar códigos USSD arbitrarios o acceder directamente a los parsers:

#### Kotlin

```kotlin
sim.ussdFetch("*222#", object : UssdStringCallback {
    override fun onSuccess(rawResponse: String) {
        val balance = rawResponse.extractMainBalance()
    }
    override fun onFailure(throwable: Throwable) { }
})
```

#### Java

```java
SimCardUtils.ussdFetch(sim, "*222#", new UssdStringCallback() {
    @Override
    public void onSuccess(String rawResponse) { }
    @Override
    public void onFailure(Throwable throwable) { }
});
```

Los parsers también pueden usarse directamente:

| Parser | Devuelve |
|---|---|
| `MainBalanceParser.extractMainBalance(input)` | `MainBalance` |
| `MainDataParser.extractMainData(input)` | `MainData` |
| `MainVoiceParser.extractVoice(input)` | `VoiceBalance` |
| `MainSmsParser.extractSms(input)` | `MessagesBalance` |
| `BonusBalanceParser.extractBonusBalance(input)` | `BonusBalance` |

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
