# Système de Diagnostic Médical (Logique de Description)

Ce projet est une application Java basée sur la bibliothèque **TweetyProject** (et plus spécifiquement son module de Logiques de Description `org.tweetyproject.logics.dl`). Il a été conçu pour le TP2 de Représentation des Connaissances et Raisonnement (KR), en proposant une interface graphique Swing moderne, destinée préférentiellement à une présentation orale.

## 📋 Description du Projet

Le système modélise une base de connaissances médicale rudimentaire en utilisant la Logique de Description (DL). Son fonctionnement s'appuie sur deux composantes principales :

- **TBox (Terminological Box) :** Définit la taxonomie et les concepts médicaux.
  - *Maladies :* Grippe (`Flu`), `Covid-19` (`Covid`), Rhume (`Cold`).
  - *Symptômes :* Fièvre (`HasFever`), Toux (`HasCough`), Fatigue (`HasFatigue`), Perte de goût (`HasLossOfTaste`), Mal de gorge (`HasSoreThroat`), Courbatures (`HasBodyAche`).
  - *Définitions (Axiomes d'Équivalence) :* Par exemple, `Covid ≡ HasFever ⊓ HasLossOfTaste`.

- **ABox (Assertional Box) :** Construite dynamiquement à partir des saisies de l'utilisateur.
  - Affirme l'existence d'un patient et de ses symptômes sous forme d'assertions de concepts (ex. `Patient : HasFever`).

Le système utilise ensuite le moteur d'inférence **NaiveDlReasoner** pour vérifier, par énumération des interprétations possibles, à quelle maladie correspond le profil du patient entré, en interrogeant la KB (Knowledge Base).

## ✨ Fonctionnalités

- **Interface Utilisateur Moderne :** GUI construite avec Java Swing, arborant un "Dark Medical Theme" épuré.
- **Saisie interactive :** Les symptômes sont facilement activables via des cases à cocher.
- **Raisonnement Logique Transparent :** Le journal de raisonnement s'affiche sur le panneau de droite, permettant de visualiser pas à pas le déroulement (Chargement de la TBox, Construction de l'ABox, Requêtes d'appartenance au raisonneur).
- **Diagnostics Multiples :** Capacité d'identifier le Covid-19, la Grippe ou un Rhume de façon logique.

## 🛠️ Technologies Utilisées

- **Java 26**
- **Maven** pour la gestion du projet et des dépendances.
- **TweetyProject (`tweety-full` v1.23)** pour la modélisation et le raisonnement DL (`DlBeliefSet`, `NaiveDlReasoner`, `EquivalenceAxiom`, `ConceptAssertion`, `Intersection`, etc.).
- **Java Swing** pour l'interface graphique.

## 🚀 Comment exécuter le projet

### Prérequis
- Un environnement d'exécution **Java (JDK) 26** (ou version compatible)
- **Maven** installé sur votre machine.

### Instructions

1. **Ouvrez un terminal** dans le répertoire racine du projet (là où se trouve le fichier `pom.xml`).
2. **Compilez le projet via Maven :**
   ```bash
   mvn clean compile
   ```
3. **Exécutez le programme principal :**
   Lancez la classe `MainTP2Description` à l'aide de la commande :
   ```bash
   mvn exec:java -Dexec.mainClass="com.tp.kr.MainTP2Description"
   ```
   *(Alternativement, vous pouvez exécuter le fichier `MainTP2Description.java` directement depuis votre IDE favori tel que IntelliJ IDEA, Eclipse ou VS Code).*

## 📂 Structure du projet

Voici un aperçu des fichiers clés du dépôt :
- `pom.xml` : Le descripteur de projet Maven listant le compilateur Java et les dépendances externes (TweetyProject, JUnit).
- `src/main/java/com/tp/kr/MainTP2Description.java` : Le point d'entrée principal. Il contient à la fois l'interface graphique Swing et l'implémentation complète des modèles et de la logique de raisonnement DL.

---
*Ce projet a été réalisé dans le cadre des travaux pratiques de Représentation des Connaissances et Raisonnement (TP2).*
