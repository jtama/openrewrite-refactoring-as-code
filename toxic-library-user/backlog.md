# Unit tests backlog

## Contexte Technique
* **Langage :** Java 8 (attention : pas de fonctionnalités de Java 9+)
* **Framework de Test :** JUnit 4
* **Framework de Mocking :** Mockito
* 
---
### **Classe à tester (CUT) :** `toxic-library-user/src/main/java/com/github/jtama/app/reservation/Reservation.java`

* **Statut**: A_FAIRE
* **Fichier de test cible :** `com.github.jtama.app.reservation.Reservation`

#### Cartographie des méthodes à couvrir

| Méthode à tester | Signature de la méthode | Statut |
| :--- | :--- | :--- |
| `existsByUserNameAndMonthAndHostelName` | `public static Boolean existsByUserNameAndMonthAndHostelName(String user, int month, String name)` | `A_FAIRE` |
| `findByUserNameAndMonthAndHostelIsNotNull` | `public static Optional<Reservation> findByUserNameAndMonthAndHostelIsNotNull(String user, int month)` | `A_FAIRE` |
| `existsByMonthAndRocketName` | `public static Boolean existsByMonthAndRocketName(int month, String name)` | `A_FAIRE` |
| `Reservation` | `public Reservation()` | `A_FAIRE` |
| `Reservation` | `public Reservation(String userName, int month)` | `A_FAIRE` |
| `getUserName` | `public String getUserName()` | `A_FAIRE` |
| `setUserName` | `public void setUserName(String userName)` | `A_FAIRE` |
| `getMonth` | `public int getMonth()` | `A_FAIRE` |
| `setMonth` | `public void setMonth(int month)` | `A_FAIRE` |
| `getHostel` | `public Hostel getHostel()` | `A_FAIRE` |
| `setHostel` | `public void setHostel(Hostel house)` | `A_FAIRE` |
| `getRocket` | `public Rocket getRocket()` | `A_FAIRE` |
| `setRocket` | `public void setRocket(Rocket rocket)` | `A_FAIRE` |
---
### **Classe à tester (CUT) :** `toxic-library-user/src/main/java/com/github/jtama/app/exception/UnknownEntityException.java`

* **Statut**: A_FAIRE
* **Fichier de test cible :** `com.github.jtama.app.exception.UnknownEntityException`

#### Cartographie des méthodes à couvrir

| Méthode à tester | Signature de la méthode | Statut |
| :--- | :--- | :--- |
| `UnknownEntityException` | `public UnknownEntityException(String message)` | `A_FAIRE` |
---
### **Classe à tester (CUT) :** `toxic-library-user/src/main/java/com/github/jtama/app/exception/InvalidBookingException.java`

* **Statut**: A_FAIRE
* **Fichier de test cible :** `com.github.jtama.app.exception.InvalidBookingException`

#### Cartographie des méthodes à couvrir

| Méthode à tester | Signature de la méthode | Statut |
| :--- | :--- | :--- |
| `InvalidBookingException` | `public InvalidBookingException(String message)` | `A_FAIRE` |
---
### **Classe à tester (CUT) :** `toxic-library-user/src/main/java/com/github/jtama/app/rocket/RocketReservationService.java`

* **Statut**: A_FAIRE
* **Fichier de test cible :** `com.github.jtama.app.rocket.RocketReservationService`

#### Cartographie des méthodes à couvrir

| Méthode à tester | Signature de la méthode | Statut |
| :--- | :--- | :--- |
| `book` | `public Reservation book(String name, int month, String user)` | `A_FAIRE` |
---
### **Classe à tester (CUT) :** `toxic-library-user/src/main/java/com/github/jtama/app/security/HeaderAuthenticationRequest.java`

* **Statut**: A_FAIRE
* **Fichier de test cible :** `com.github.jtama.app.security.HeaderAuthenticationRequest`

#### Cartographie des méthodes à couvrir

| Méthode à tester | Signature de la méthode | Statut |
| :--- | :--- | :--- |
| `HeaderAuthenticationRequest` | `public HeaderAuthenticationRequest(String userName, Set<String> roles)` | `A_FAIRE` |
| `getPrincipal` | `public Principal getPrincipal()` | `A_FAIRE` |
| `getRoles` | `public Set<String> getRoles()` | `A_FAIRE` |
---
### **Classe à tester (CUT) :** `toxic-library-user/src/main/java/com/github/jtama/app/hostels/HostelController.java`

* **Statut**: A_FAIRE
* **Fichier de test cible :** `com.github.jtama.app.hostels.HostelController`

#### Cartographie des méthodes à couvrir

| Méthode à tester | Signature de la méthode | Statut |
| :--- | :--- | :--- |
| `getAll` | `public List<Hostel> getAll()` | `A_FAIRE` |
| `create` | `public Hostel create(Hostel hostel)` | `A_FAIRE` |
| `book` | `public Reservation book(@PathParam("name")String name, @QueryParam("month") Integer month, @Context SecurityContext security)` | `A_FAIRE` |
---
### **Classe à tester (CUT) :** `toxic-library-user/src/main/java/com/github/jtama/app/exception/OnerentExceptionHandler.java`

* **Statut**: A_FAIRE
* **Fichier de test cible :** `com.github.jtama.app.exception.OnerentExceptionHandler`

#### Cartographie des méthodes à couvrir

| Méthode à tester | Signature de la méthode | Statut |
| :--- | :--- | :--- |
| `toResponse` | `public Response toResponse(Exception exception)` | `A_FAIRE` |
---
### **Classe à tester (CUT) :** `toxic-library-user/src/main/java/com/github/jtama/app/exception/DuplicateEntityException.java`

* **Statut**: A_FAIRE
* **Fichier de test cible :** `com.github.jtama.app.exception.DuplicateEntityException`

#### Cartographie des méthodes à couvrir

| Méthode à tester | Signature de la méthode | Statut |
| :--- | :--- | :--- |
| `DuplicateEntityException` | `public DuplicateEntityException(String message)` | `A_FAIRE` |
---
### **Classe à tester (CUT) :** `toxic-library-user/src/main/java/com/github/jtama/app/exception/UnavailableException.java`

* **Statut**: A_FAIRE
* **Fichier de test cible :** `com.github.jtama.app.exception.UnavailableException`

#### Cartographie des méthodes à couvrir

| Méthode à tester | Signature de la méthode | Statut |
| :--- | :--- | :--- |
| `UnavailableException` | `public UnavailableException(String message)` | `A_FAIRE` |
---
### **Classe à tester (CUT) :** `toxic-library-user/src/main/java/com/github/jtama/app/rocket/RocketType.java`

* **Statut**: A_FAIRE
* **Fichier de test cible :** `com.github.jtama.app.rocket.RocketType`

#### Cartographie des méthodes à couvrir

| Méthode à tester | Signature de la méthode | Statut |
| :--- | :--- | :--- |
| `RocketType` | `RocketType(String luxury)` | `A_FAIRE` |
| `getLuxury` | `public String getLuxury()` | `A_FAIRE` |
| `is` | `public int is(RocketType type)` | `A_FAIRE` |
---
### **Classe à tester (CUT) :** `toxic-library-user/src/main/java/com/github/jtama/app/hostels/HostelReservationService.java`

* **Statut**: A_FAIRE
* **Fichier de test cible :** `com.github.jtama.app.hostels.HostelReservationService`

#### Cartographie des méthodes à couvrir

| Méthode à tester | Signature de la méthode | Statut |
| :--- | :--- | :--- |
| `book` | `public Reservation book(String name, int month, String userName)` | `A_FAIRE` |
---
### **Classe à tester (CUT) :** `toxic-library-user/src/main/java/com/github/jtama/app/security/NaiveAuth.java`

* **Statut**: A_FAIRE
* **Fichier de test cible :** `com.github.jtama.app.security.NaiveAuth`

#### Cartographie des méthodes à couvrir

| Méthode à tester | Signature de la méthode | Statut |
| :--- | :--- | :--- |
| `authenticate` | `public Uni<SecurityIdentity> authenticate(RoutingContext context,
                                              IdentityProviderManager identityProviderManager)` | `A_FAIRE` |
| `getChallenge` | `public Uni<ChallengeData> getChallenge(RoutingContext context)` | `A_FAIRE` |
| `getCredentialTypes` | `public Set<Class<? extends AuthenticationRequest>> getCredentialTypes()` | `A_FAIRE` |
| `getCredentialTransport` | `public Uni<HttpCredentialTransport> getCredentialTransport(RoutingContext context)` | `A_FAIRE` |
---
### **Classe à tester (CUT) :** `toxic-library-user/src/main/java/com/github/jtama/app/hostels/IHostelController.java`

* **Statut**: A_FAIRE
* **Fichier de test cible :** `com.github.jtama.app.hostels.IHostelController`

#### Cartographie des méthodes à couvrir

| Méthode à tester | Signature de la méthode | Statut |
| :--- | :--- | :--- |
| `getAll` | `List<Hostel> getAll()` | `A_FAIRE` |
| `create` | `Hostel create(Hostel hostel)` | `A_FAIRE` |
| `book` | `Reservation book(@PathParam("name")String name, @QueryParam("month") Integer month, @Context SecurityContext security)` | `A_FAIRE` |
---
### **Classe à tester (CUT) :** `toxic-library-user/src/main/java/com/github/jtama/app/exception/InvalidNameException.java`

* **Statut**: A_FAIRE
* **Fichier de test cible :** `com.github.jtama.app.exception.InvalidNameException`

#### Cartographie des méthodes à couvrir

| Méthode à tester | Signature de la méthode | Statut |
| :--- | :--- | :--- |
| `InvalidNameException` | `public InvalidNameException(String message)` | `A_FAIRE` |
---
### **Classe à tester (CUT) :** `toxic-library-user/src/main/java/com/github/jtama/app/security/NaiveSecurityProvider.java`

* **Statut**: A_FAIRE
* **Fichier de test cible :** `com.github.jtama.app.security.NaiveSecurityProvider`

#### Cartographie des méthodes à couvrir

| Méthode à tester | Signature de la méthode | Statut |
| :--- | :--- | :--- |
| `getRequestType` | `public Class<HeaderAuthenticationRequest> getRequestType()` | `A_FAIRE` |
| `authenticate` | `public Uni<SecurityIdentity> authenticate(HeaderAuthenticationRequest request, AuthenticationRequestContext context)` | `A_FAIRE` |
---
### **Classe à tester (CUT) :** `toxic-library-user/src/main/java/com/github/jtama/app/util/MonthValidator.java`

* **Statut**: A_FAIRE
* **Fichier de test cible :** `com.github.jtama.app.util.MonthValidator`

#### Cartographie des méthodes à couvrir

| Méthode à tester | Signature de la méthode | Statut |
| :--- | :--- | :--- |
| `validateMonth` | `public void validateMonth(int month)` | `A_FAIRE` |
---
### **Classe à tester (CUT) :** `toxic-library-user/src/main/java/com/github/jtama/app/rocket/Rocket.java`

* **Statut**: A_FAIRE
* **Fichier de test cible :** `com.github.jtama.app.rocket.Rocket`

#### Cartographie des méthodes à couvrir

| Méthode à tester | Signature de la méthode | Statut |
| :--- | :--- | :--- |
| `findByName` | `public static Optional<Rocket> findByName(String name)` | `A_FAIRE` |
| `persistIfNotExists` | `public static Rocket persistIfNotExists(Rocket rocket)` | `A_FAIRE` |
| `setName` | `public void setName(String name)` | `A_FAIRE` |
| `setType` | `public void setType(RocketType type)` | `A_FAIRE` |
| `getName` | `public String getName()` | `A_FAIRE` |
| `getType` | `public RocketType getType()` | `A_FAIRE` |
---
### **Classe à tester (CUT) :** `toxic-library-user/src/main/java/com/github/jtama/app/hostels/Hostel.java`

* **Statut**: A_FAIRE
* **Fichier de test cible :** `com.github.jtama.app.hostels.Hostel`

#### Cartographie des méthodes à couvrir

| Méthode à tester | Signature de la méthode | Statut |
| :--- | :--- | :--- |
| `persistIfNotExists` | `public static Hostel persistIfNotExists(Hostel hostel)` | `A_FAIRE` |
| `findByName` | `public static Optional<Hostel> findByName(String name)` | `A_FAIRE` |
| `getName` | `public String getName()` | `A_FAIRE` |
| `setName` | `public void setName(String name)` | `A_FAIRE` |
---
### **Classe à tester (CUT) :** `toxic-library-user/src/main/java/com/github/jtama/app/rocket/RocketController.java`

* **Statut**: A_FAIRE
* **Fichier de test cible :** `com.github.jtama.app.rocket.RocketController`

#### Cartographie des méthodes à couvrir

| Méthode à tester | Signature de la méthode | Statut |
| :--- | :--- | :--- |
| `getAll` | `public List<Rocket> getAll()` | `A_FAIRE` |
| `create` | `public Response create(@Valid Rocket rocket)` | `A_FAIRE` |
| `book` | `public Reservation book(@PathParam("name") String name, @QueryParam("month") Integer month, @Context SecurityContext securityContext)` | `A_FAIRE` |
---
