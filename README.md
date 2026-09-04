Hydration Tracker
AquaTrack est une application mobile Android moderne et intuitive conçue pour suivre la consommation d'eau quotidienne et encourager l'atteinte des objectifs d'hydratation.

Fonctionnalités
Suivi en Temps Réel : Jauge circulaire dynamique affichant la progression en millilitres par rapport à l'objectif quotidien (par défaut : 2000 ml).

Ajout Rapide : Boutons d'action optimisés pour consigner instantanément 100 ml, 250 ml ou 500 ml d'eau.

Indicateur Visuel : Jauge de progression secondaire sous forme de gouttes d'eau interactives.

Historique Journalier : Liste défilable des derniers ajouts avec leur horodatage précis.

Réinitialisation : Fonctionnalité de remise à zéro du compteur via une boîte de dialogue sécurisée.

Technologies & Architecture
Langage : Kotlin

Interface Graphique : Jetpack Compose (Material Design 3)

Architecture : MVVM (Model-View-ViewModel)

Gestion d'État : StateFlow et Kotlin Coroutines

Compatibilité : Android 7.0 (API Level 24) et supérieur

Installation & Utilisation
Prérequis
Android Studio à jour.

JDK 11 configuré pour la compilation (JavaVersion.VERSION_11).

Lancement local
Clonez ce dépôt sur votre machine :

Bash
git clone https://github.com/axel225-Ci/Hydration-Tracker.git
Ouvrez le projet dans Android Studio.

Attendez la fin de la synchronisation Gradle.

Lancez le projet sur un émulateur ou un téléphone Android physique branché en mode débogage USB.

Notes de Configuration (Développeurs)
Alignement JVM : Le projet est configuré pour compiler en Java 11. Si vous modifiez l'environnement de compilation, assurez-vous que sourceCompatibility, targetCompatibility et kotlinOptions { jvmTarget } sont synchronisés dans le fichier build.gradle.kts.

Intégration Firebase : L'infrastructure du projet est prête pour l'intégration des services Firebase (AI, AppCheck). Pour activer ces fonctionnalités, ajoutez un fichier google-services.json valide dans le dossier app/ et réactivez les dépendances commentées dans le fichier Gradle de l'application.
