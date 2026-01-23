# estágio de BUILD
FROM maven:3.9.12-sapmachine-21 as build
WORKDIR /build

# primeiro ponto copia arquivos da pasta root (neste caso, tudo da pasta root da API, pois tem o ponto sem especificação)
# segundo ponto copia arquivos definidos no primeiro ponto para dentro da pasta /build
COPY . .

# compila e empacota a aplicação, gerando um .jar dentro da pasta /target
RUN mvn clean package -DskipTests

# estágio de RUN
FROM sapmachine:21.0.6
WORKDIR /app

# copia todos os arquivos de pacote da aplicação (.jar), renomeia para libraryapi.jar e copia para dentro da pasta /app
COPY --from=build ./build/target/*.jar ./libraryapi.jar

# exporta as portas da aplicação e do spring actuator, respectivamente
EXPOSE 8080
EXPOSE 9090

# Definindo as variáveis de ambiente do container
# (valores após '=' são valores padrão caso não seja definido a variavel quando subir a aplicação)
ENV DATASOURCE_URL=''
ENV DATASOURCE_USERNAME=''
ENV DATASOURCE_PASSWORD=''
ENV GOOGLE_CLIENT_ID=''
ENV GOOGLE_CLIENT_SECRET=''
ENV SPRING_PROFILES_ACTIVE='production'
# definindo timezone do container independente da localização do servidor
ENV TZ_='America/Sao_Paulo'

# roda a aplicação empacotada dentro da pasta /app (.jar)
ENTRYPOINT java -jar libraryapi.jar