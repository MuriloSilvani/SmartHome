#define quarto2 2
#define quarto1 3
#define cozinha 4
#define sala 5
#define banheiro 6
#define quarto3 7
#define varanda1 8
#define varanda2 9

#define luminosidade A0
#define echoPin 12
#define trigPin 13
#define alarme 10
//#define temperatura A2


String smarthome = "";
char inputSerial;
int luminosidadeExterna = 0;
float proximidade = 0;
int statusAlarme = 1;
int statusBuzz = 0;
float temperaturaC = 0;
float umidade = 0;

void setup()
{
  Serial.begin(9600);
  
  pinMode(sala, OUTPUT);
  pinMode(cozinha, OUTPUT);
  pinMode(quarto1, OUTPUT);
  pinMode(quarto2, OUTPUT);
  pinMode(quarto3, OUTPUT);
  pinMode(banheiro, OUTPUT);
  pinMode(varanda1, OUTPUT);
  pinMode(varanda2, OUTPUT);
  
  pinMode(luminosidade, INPUT);
  pinMode(trigPin, OUTPUT);
  pinMode(echoPin, INPUT);
//  pinMode(temperatura, INPUT);
  pinMode(alarme, OUTPUT);
}

void loop()
{

//  temperaturaC = (((float)analogRead(temperatura)*5/1023) - 0.5) * 100;
  temperaturaC = (((float)random(20,358)*5/1023) - 0.5) * 100;
//  Serial.println("Temperatura:");Serial.println(temperaturaC);delay(500);
  
  luminosidadeExterna = analogRead(luminosidade);
//  Serial.println("Luminosidade:");Serial.println(luminosidadeExterna);delay(500);
  if (luminosidadeExterna > 400) {
    digitalWrite(varanda1, LOW);
    digitalWrite(varanda2, LOW);
  }else{
    digitalWrite(varanda1, HIGH);
    digitalWrite(varanda2, HIGH);
  }

  digitalWrite(trigPin, HIGH);
  delayMicroseconds(20);
  digitalWrite(trigPin, LOW);
  int duration = pulseIn(echoPin, HIGH);
  proximidade = (duration / 2) / 29.1;
//  Serial.println("Proximidade:");Serial.println(proximidade);delay(500);

  if(statusAlarme == 1){
    if(proximidade > 0 && proximidade < 7){
       statusBuzz = 1;
    }
    if(statusBuzz == 1){
      for (int buzz = 550; buzz > 450; buzz -= 1){
        tone(alarme, buzz); 
        delay(1);
      }
      for (int buzz = 450; buzz < 550; buzz += 1){
        tone(alarme, buzz); 
        delay(1);
      }  
    }
  }else{
    noTone(alarme);
    statusBuzz = 0;
  }


  umidade = (float)random(20,80);
  

 smarthome  = "";
 smarthome += temperaturaC;
 smarthome += ";";
 smarthome += luminosidadeExterna;
 smarthome += ";";
 smarthome += proximidade;
 smarthome += ";";
 smarthome += umidade;
 smarthome += ";";

 Serial.println(smarthome);
  
  if(Serial.available()){
    inputSerial = Serial.read();
    switch(inputSerial){
      case '0':
      digitalWrite(sala, LOW);
      digitalWrite(cozinha, LOW);
      digitalWrite(quarto1, LOW);
      digitalWrite(quarto2, LOW);
      digitalWrite(quarto3, LOW);
      digitalWrite(banheiro, LOW);
      break;
      case '1':
      digitalWrite(sala, HIGH);
      digitalWrite(cozinha, HIGH);
      digitalWrite(quarto1, HIGH);
      digitalWrite(quarto2, HIGH);
      digitalWrite(quarto3, HIGH);
      digitalWrite(banheiro, HIGH);
      break;
      case '2':
      digitalWrite(sala, !digitalRead(sala));
      break;
      case '3':
      digitalWrite(cozinha, !digitalRead(cozinha));
      break;
      case '4':
      digitalWrite(quarto1, !digitalRead(quarto1));
      break;
      case '5':
      digitalWrite(quarto2, !digitalRead(quarto2));
      break;
      case '6':
      digitalWrite(quarto3, !digitalRead(quarto3));
      break;
      case '7':
      digitalWrite(banheiro, !digitalRead(banheiro));
      break;
      case '8':
      if(statusAlarme == 1){
        statusAlarme = 0;
        statusBuzz = 0;
      }else{
        statusAlarme = 1;
      }
      break;
      case '9':
      statusAlarme = 1;
      statusBuzz = 1;
      break;
    }
  }
}
