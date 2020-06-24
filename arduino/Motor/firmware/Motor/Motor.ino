
// Left Motor
int dpin_LM_bwd = 7;
int dpin_LM_fwd = 4;
int ppin_LM_speed = 5;
int arrLM[3] = {0, 0, 210};
 
// Right Motor
int dpin_RM_bwd = 12;
int dpin_RM_fwd = 13;
int ppin_RM_speed = 10;
int arrRM[3] = {0, 0, 210};

String separator = ";";
String changed = "start";

void setup()
{

  //Liga serial
  Serial.begin(115200);
  Serial.flush();

  while (!Serial) {
   ; // wait for serial port to connect. Needed for native USB port only
  }
  
  //Sobe pinos para motor esquerdo
  pinMode(dpin_LM_bwd,OUTPUT);
  pinMode(dpin_LM_fwd,OUTPUT);
  pinMode(ppin_LM_speed,OUTPUT);

  //Sobe pinos para motor direito
  pinMode(dpin_RM_bwd,OUTPUT);
  pinMode(dpin_RM_fwd,OUTPUT);
  pinMode(ppin_RM_speed,OUTPUT);
  
  // prints title with ending line break
  Serial.println("ARDUINO BY LUCAS IORIO - ON");
}
 
void loop()
{
  String showStatus = "";
  String received = "";
  int i=0;

  // Le string recebida
  if (Serial.available() > 0){
    received = readStringSerial();
    received = String(received);//resolve um bug
    
    //Pega tamanho da string recebida, mais um para o terminador
    int str_len = received.length() + 1;
    
    //Converte tudo para array
    char *str;
    char *p;
    received.toCharArray(p, str_len);
    
    //Separa conteudo
    int count=0;
    while ((str = strtok_r(p, ";", &p)) != NULL){
      int val=atoi(str);//le valores entre ;

      //Carrega variaveis para alterar device
      if (count >= 0 && count <= 2){
        //Carrega motor esquerdo
        arrLM[count] = val;

      }else if (count >= 3 && count <= 5){
        //Carrega motor direito
        arrRM[count-3] = val;
      }
      
      count++;
    }
  }

  //*********** Chama todos os devices
  if (changed != received){
    changed = received;
    showStatus += rightMotor(received);
    showStatus += leftMotor(received);
    
    //Mostrar o status dos devices
    Serial.println(showStatus);    
  }
  


  delay(500);
}

String leftMotor(String received){
  //Move para frente
  if (arrLM[0] == 1){
    digitalWrite(dpin_LM_fwd, HIGH);
  }else{
    digitalWrite(dpin_LM_fwd, LOW);
  }
  delay(20);
   
  //Altera rotacao para Traz,
  if (arrLM[1] == 1){
    digitalWrite(dpin_LM_bwd, HIGH);
  }else{
    digitalWrite(dpin_LM_bwd, LOW);
  }
  delay(20);
   
  //altera potencia do giro 
  analogWrite(ppin_LM_speed, arrLM[2]);
  delay(20);
  
  return String(arrLM[0]) + separator + String(arrLM[1]) + separator +  String(arrLM[2] + separator);
}

String rightMotor(String received){
  if (arrRM[0] == 1){
    digitalWrite(dpin_RM_fwd, HIGH);
  }else{
    digitalWrite(dpin_RM_fwd, LOW);
  }
  delay(20);
   
  //Altera rotacao para Traz
  if (arrRM[1] == 1){
    digitalWrite(dpin_RM_bwd, HIGH);
  }else{
    digitalWrite(dpin_RM_bwd, LOW);
  }
  delay(20);

  //altera potencia do giro 
  analogWrite(ppin_RM_speed, arrRM[2]);
  delay(20);
     
  return String(arrRM[0]) + separator + String(arrRM[1]) + separator +  String(arrRM[2]) + separator;
}

String readStringSerial(){
  String conteudo = "";
  char caractere;
  
  // Enquanto receber algo pela serial
  while(Serial.available() > 0) {
    // Lê byte da serial
    caractere = Serial.read();
    // Ignora caractere de quebra de linha
    if (caractere != '\n'){
      // Concatena valores
      conteudo.concat(caractere);
    }
    // Aguarda buffer serial ler próximo caractere
    delay(10);
  }
  return conteudo;
}
