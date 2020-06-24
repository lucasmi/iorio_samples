#define LED_VERDE 2
#define LED_AMARELO 3
#define LED_VERMELHO 4
#define LED_AZUL 6

#define BOTAO_START 9
#define BOTAO_VERDE 10
#define BOTAO_AMARELO 11
#define BOTAO_VERMELHO 12
#define BOTAO_AZUL 13
#define INDEFINIDO -1

#define UM_SEGUNDO 1000
#define MEIO_SEGUNDO 500

#define TAMANHO_SEQUENCIA 20

#define JOGO_DESLIGADO 0
#define JOGO_VEZ_MAQUINA 1
#define JOGO_VEZ_HUMANO 2
#define JOGO_FIM 3

#define BOTOES_PARADO "1111"

String sequenciaLuzes[TAMANHO_SEQUENCIA];

//Animacao inicial
int animationFrame = 0;
unsigned long animationTime = millis();
unsigned long answerTime = millis();

//jogo
int gameStatus=JOGO_DESLIGADO;
int gamePosition=0;
int answerPosition=0;
int playerPosition=0;
String resposta = BOTOES_PARADO;
  
void setup() {
  Serial.begin(9600);
  randomSeed(analogRead(0));
  iniciaPortas();
}

void iniciaPortas(){
  pinMode(LED_VERDE, OUTPUT);
  pinMode(LED_AMARELO, OUTPUT);
  pinMode(LED_VERMELHO, OUTPUT);
  pinMode(LED_AZUL, OUTPUT);  

  pinMode(BOTAO_START, INPUT_PULLUP);
  pinMode(BOTAO_VERDE, INPUT_PULLUP);
  pinMode(BOTAO_AMARELO, INPUT_PULLUP);
  pinMode(BOTAO_VERMELHO, INPUT_PULLUP);
  pinMode(BOTAO_AZUL, INPUT_PULLUP);    
}

void loop() {
  if (gameStatus == JOGO_DESLIGADO){
    animacaoInicialLed();
    verificaIniciodeJogo();
    
  }else if(gameStatus == JOGO_VEZ_MAQUINA){
    vezMaquina();
    
  }else if(gameStatus == JOGO_VEZ_HUMANO){
    vezHumano();
     
  }else if(gameStatus == JOGO_FIM){
    gameStatus = JOGO_DESLIGADO;
    Serial.println("JOGO DESLIGADO");
    
  }
}

void verificaIniciodeJogo(){
  if(digitalRead(BOTAO_START) == LOW){
    iniciaJogo();
  }
}

void iniciaJogo(){
  Serial.println("INICIA JOGO");
  
  //mostra que o jogo comecou
  piscaTodos();

  //Carrega a sequencia
  for(int indice = 0; indice < TAMANHO_SEQUENCIA; indice ++){
    sequenciaLuzes[indice] = sorteiaCor();
  }

  //comeca jogo
  gamePosition = 0;
  apagaTodosLeds();
  gameStatus = JOGO_VEZ_MAQUINA;
}

boolean verificaSeGanhou(){
    //verifica se ganhou o jogo
  if (gamePosition >= TAMANHO_SEQUENCIA -1){
    Serial.println("GANHOU O JOGO");
    piscaTodos();
    piscaTodos();
    gameStatus = JOGO_FIM;
    return true;
  }else{
    gamePosition++;
    return false;
  }
  
}

void vezMaquina(){

  //percorre o array para mostrar os leds
  //ate a posicao atual do jogo
  for(int indice = 0; indice <= gamePosition; indice ++){
    piscaLedes(sequenciaLuzes[indice]);
    Serial.println("VEZ MAQUINA:" + sequenciaLuzes[indice]);

    delay(MEIO_SEGUNDO);
    apagaTodosLeds();
  }
  
  //passa o jogo para o humano
  answerPosition=0;
  answerTime=0;
  resposta = BOTOES_PARADO;
  gameStatus=JOGO_VEZ_HUMANO;

}

void vezHumano(){
  //coleta resposta
  String respAtual = acendeLed();

  //se foi clicado em algo armazena a resposta
  if (respAtual != BOTOES_PARADO){
   resposta = respAtual;
  }

  //se deu o tempo para verificar a resposta
  if(millis()-answerTime >= 300 && answerTime != 0){
     //apaga leds
     apagaTodosLeds();
     delay(UM_SEGUNDO);
     
     Serial.println("VEZ HUMANO: " + resposta);
     Serial.println("VERIFICA SE CONFERE COM: " + sequenciaLuzes[answerPosition]);
     
    //verifica se a reposta esta certa
    if(resposta == sequenciaLuzes[answerPosition]){

      //se a posicao maxima atingiu vai para a proxima
      if (gamePosition == answerPosition){
        Serial.println("ATINGIU A POSICAO MAXIMA, PASSAR MAQUINA");
        
        //sobe posicao
        if (verificaSeGanhou() == false){
          //chama a maquina
          vezMaquina();  
        }

      }else{
        Serial.println("PROXIMA SEQUENCIA HUMANO");
        
        //vai para a proxima resposta
        answerPosition++;

        //reinicia tempo para a respsota
        answerTime = 0;
        resposta = BOTOES_PARADO;
      }
            
    }else{
      gameStatus = JOGO_FIM;
    }
  }

  //comeca a contaro tempo que tem para apertar todos os botoes
  if (resposta != BOTOES_PARADO && answerTime == 0){
    Serial.println("INICIOU A REPOSTA");
    answerTime = millis();
  }
}

void animacaoInicialLed(){
  if(millis()-animationTime >= 100){
    animationFrame++;

    if (animationTime <  millis()){
      animationTime = millis();  
    }else{
      animationTime=0;
    }
  }
  
  if(animationFrame==0){
     digitalWrite(LED_AZUL, LOW);
     digitalWrite(LED_VERDE, HIGH);
  }else if(animationFrame==1){
     digitalWrite(LED_VERDE, LOW);    
     digitalWrite(LED_AMARELO, HIGH);
  }else if(animationFrame==2){
     digitalWrite(LED_AMARELO, LOW);
     digitalWrite(LED_VERMELHO, HIGH);
  }else if(animationFrame==3){
     digitalWrite(LED_VERMELHO, LOW);
     digitalWrite(LED_AZUL, HIGH);
  }else if(animationFrame>=4){
     animationFrame=0;
  }
}


void apagaTodosLeds(){
  digitalWrite(LED_VERDE, LOW);
  digitalWrite(LED_AMARELO, LOW);
  digitalWrite(LED_VERMELHO, LOW);
  digitalWrite(LED_AZUL, LOW);
}

void piscaTodos(){
  digitalWrite(LED_VERDE, HIGH);
  digitalWrite(LED_AMARELO, HIGH);
  digitalWrite(LED_VERMELHO, HIGH);
  digitalWrite(LED_AZUL, HIGH);
  delay(UM_SEGUNDO);
  digitalWrite(LED_VERDE, LOW);
  digitalWrite(LED_AMARELO, LOW);
  digitalWrite(LED_VERMELHO, LOW);
  digitalWrite(LED_AZUL, LOW);
  delay(MEIO_SEGUNDO);
}

String sorteiaCor(){
  String retorno = String(random(0,2));
  retorno += String(random(0,2));
  retorno += String(random(0,2));
  retorno += String(random(0,2));

  if (retorno == BOTOES_PARADO){
    retorno = "0001";
  }
  
  return retorno;
}

void piscaLedes(String sequencia){
  //desliga todos
  digitalWrite(LED_VERDE, LOW);
  digitalWrite(LED_AMARELO, LOW);
  digitalWrite(LED_VERMELHO, LOW);
  digitalWrite(LED_AZUL, LOW);
  delay(MEIO_SEGUNDO);
     
  //acende somente os necessarios
  if(sequencia.substring(0,1) == "0"){
    digitalWrite(LED_VERDE, HIGH);
  }
  
  if(sequencia.substring(1,2) == "0"){
    digitalWrite(LED_AMARELO, HIGH);
  }
  
  if(sequencia.substring(2,3) == "0"){
    digitalWrite(LED_VERMELHO, HIGH);
  }
  
  if(sequencia.substring(3,4) == "0"){
    digitalWrite(LED_AZUL, HIGH);
  }
  
  delay(UM_SEGUNDO);
}

String  acendeLed(){
  String retorno = "";
  int estadoBVerde = digitalRead(BOTAO_VERDE);
  int estadoBAmarelo = digitalRead(BOTAO_AMARELO);
  int estadoBVermelho = digitalRead(BOTAO_VERMELHO);
  int estadoBAzul = digitalRead(BOTAO_AZUL);

  if (estadoBVerde == LOW){
     digitalWrite(LED_VERDE, HIGH);
  }else{
     digitalWrite(LED_VERDE, LOW);
  }

  if (estadoBAmarelo == LOW){
     digitalWrite(LED_AMARELO, HIGH);
  }else{
     digitalWrite(LED_AMARELO, LOW);
  }

  if (estadoBVermelho == LOW){
     digitalWrite(LED_VERMELHO, HIGH);
  }else{
     digitalWrite(LED_VERMELHO, LOW);
  }

  if (estadoBAzul == LOW){
     digitalWrite(LED_AZUL, HIGH);
  }else{
     digitalWrite(LED_AZUL, LOW);
  }

  retorno += estadoBVerde;
  retorno += estadoBAmarelo;
  retorno += estadoBVermelho;
  retorno += estadoBAzul;
  
  return retorno;
}
