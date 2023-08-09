SDK Android para SuiteEtecsa
============================

[![](https://jitpack.io/v/suitetecsa/sdk-android.svg)](https://jitpack.io/#suitetecsa/sdk-android)

Una librería que facilita la creación de aplicaciones Android dedicadas a gestionar los servicios de [ETECSA](https://www.etecsa.cu).

## Cómo usar

Agrega la dependencia a tu archivo build.gradle o .kts:

```kotlin
implementation("com.github.suitetecsa.sdk-android:{última-versión}")
```

### Obtener información de las tarjetas SIM

Para obtener información sobre las tarjetas SIM insertadas en el dispositivo:

#### Kotlin
```kotlin
// Instancia SimCardsAPI
val simCardsAPI = SimCardsAPI
    .builder(context)
    .build()

// Obtiene las tarjetas SIM insertadas en el dispositivo
val simCards = simCardsAPI.getSimCards()
```

#### Java
```java
// Instancia SimCardsAPI
SimCardsAPI simCardsAPI = SimCardsAPI
    .builder(context)
    .build();

// Obtiene las tarjetas SIM insertadas en el dispositivo
List<SimCard> simCards = simCardsAPI.getSimCards();
```

### Obtener saldo de la tarjeta SIM

Para obtener el saldo de la primera tarjeta SIM de la lista:

#### Kotlin
```kotlin
// Obtiene el ID de suscripción de la primera tarjeta SIM de la lista
val subscriptionId = simCards.first().subscriptionId

// Crea un objeto TelephonyManager
val manager = (context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager)
    .createForSubscriptionId(subscriptionId)

// Instancia UssdApi
val ussdApi = UssdApi
    .builder(manager)
    .build()

// Envía una solicitud Ussd y devuelve un UssdResponse
val ussdResponse = ussdApi.sendUssdRequest("*222#")

// Convierte el objeto UssdResponse en un objeto MainBalance
val mainBalance = ussdResponse.parseMainBalance()
```

#### Java
```java
// Obtiene el ID de suscripción de la primera tarjeta SIM de la lista
int subscriptionId = simCards.get(0).getSubscriptionId();

// Crea un objeto TelephonyManager
TelephonyManager manager = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE))
    .createForSubscriptionId(subscriptionId);

// Instancia UssdApi
UssdApi ussdApi = UssdApi
    .builder(manager)
    .build();

// Envía una solicitud Ussd y devuelve un UssdResponse
UssdResponse ussdResponse = ussdApi.sendUssdRequest("*222#");

// Convierte el objeto UssdResponse en un objeto MainBalance
MainBalance mainBalance = ussdResponse.parseMainBalance();
```

### Ejemplo corrutina

#### Kotlin
```kotlin
class MainActivity : AppCompatActivity() {

    private lateinit var telephonyManager: TelephonyManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inicializa el TelephonyManager
        telephonyManager = getSystemService(TelephonyManager::class.java)

        // Verifica y solicita permisos si es necesario (esto es solo un ejemplo; asegúrate de manejar los permisos correctamente)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            makeUssdRequest()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), PERMISSION_REQUEST_CODE)
        }
    }

    private fun makeUssdRequest() {
        // Crea una instancia de UssdApi utilizando el builder
        val ussdApi = UssdApi.builder(telephonyManager).build()

        // Define el código USSD que deseas enviar
        val ussdCode = "*123#" // Por ejemplo, este es un código USSD de ejemplo

        // Realiza la solicitud USSD en un CoroutineScope
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val ussdResponse = withContext(Dispatchers.IO) {
                    ussdApi.sendUssdRequest(ussdCode)
                }
                handleUssdResponse(ussdResponse)
            } catch (e: UssdException) {
                // Manejar excepciones de USSD
                e.printStackTrace()
            }
        }
    }

    private fun handleUssdResponse(ussdResponse: UssdResponse) {
        // Maneja la respuesta USSD según tus necesidades
        val message = ussdResponse.message
        // Por ejemplo, muestra la respuesta en una vista o realiza acciones basadas en la respuesta
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1
    }
}
```

#### Java
```java
public class MainActivity extends AppCompatActivity {

    private TelephonyManager telephonyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializa el TelephonyManager
        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        // Verifica y solicita permisos si es necesario (esto es solo un ejemplo; asegúrate de manejar los permisos correctamente)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            makeUssdRequest();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_REQUEST_CODE);
        }
    }

    private void makeUssdRequest() {
        // Crea una instancia de UssdApi utilizando el builder
        UssdApi ussdApi = UssdApi.builder(telephonyManager).build();

        // Define el código USSD que deseas enviar
        String ussdCode = "*123#"; // Por ejemplo, este es un código USSD de ejemplo

        // Realiza la solicitud USSD en un CoroutineScope
        new CoroutineScope(Dispatchers.Main).launch(new CoroutineScope.CallerContinuation() {
            @Override
            public void resumeWith(Object result) {
                try {
                    UssdResponse ussdResponse = withContext(Dispatchers.IO, () -> {
                        try {
                            return ussdApi.sendUssdRequest(ussdCode);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }
                    });
                    handleUssdResponse(ussdResponse);
                } catch (UssdException e) {
                    // Manejar excepciones de USSD
                    e.printStackTrace();
                }
            }

            @Override
            public Object invokeSuspend(Object result) {
                return null;
            }
        });
    }

    private void handleUssdResponse(UssdResponse ussdResponse) {
        // Maneja la respuesta USSD según tus necesidades
        String message = ussdResponse.getMessage();
        // Por ejemplo, muestra la respuesta en una vista o realiza acciones basadas en la respuesta
    }

    private static final int PERMISSION_REQUEST_CODE = 1;
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
