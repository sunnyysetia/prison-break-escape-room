[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-24ddc0f5d75046c5622901739e7c5dd533143b0c8e959d652212380cedb1ea36.svg)](https://classroom.github.com/a/K_kBlAWd)

# SOFTENG 206 - EscAIpe Room

## To setup OpenAI's API

- add in the root of the project (i.e., the same level where `pom.xml` is located) a file named `apiproxy.config`
- put inside the credentials that you received from no-reply@digitaledu.ac.nz (put the quotes "")
  `  email: “UPI@aucklanduni.ac.nz"
apiKey: “YOUR_KEY”`
  these are your credentials to invoke the OpenAI GPT APIs

## To setup codestyle's API

- add in the root of the project (i.e., the same level where `pom.xml` is located) a file named `codestyle.config`
- put inside the credentials that you received from gradestyle@digitaledu.ac.nz (put the quotes "")
  `  email: “UPI@aucklanduni.ac.nz"
accessToken: “YOUR_KEY”`
  these are your credentials to invoke gradestyle

## To run the game

`./mvnw clean javafx:run`

## To debug the game

`./mvnw clean javafx:run@debug` then in VS Code "Run & Debug", then run "Debug JavaFX"

## To run codestyle

`./mvnw clean compile exec:java@style`

## Attributions

Eraser font: https://www.1001fonts.com/eraser-font.html
Stencil font: https://fonts.adobe.com/fonts/stencil#about-section
Metropolis font: https://www.1001fonts.com/metropolis-font.html#license
