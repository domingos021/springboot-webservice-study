package com.diniz.springbootstudy.config;

import org.h2.server.web.JakartaWebServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/* ============================================================================================
 * MOTIVAÇÃO E OBJETIVO DA CRIAÇÃO DESTA CLASSE (DevConfig):
 * ============================================================================================
 *
 * 1. SUPORTE AO SPRING BOOT 3+ E JAKARTA EE:
 *    A partir do Spring Boot 3.x, o ecossistema Java migrou do antigo pacote 'javax.servlet'
 *    para o novo 'jakarta.servlet' (compatível com Tomcat 10+). A classe antiga do console H2
 *    (org.h2.server.web.WebServlet) usava 'javax' e gera erros em versões recentes.
 *    Esta classe registra explicitamente a classe 'JakartaWebServlet', resolvendo esse erro.
 *
 * 2. SEGURANÇA E ISOLAMENTO DE AMBIENTE:
 *    O H2 Console é uma ferramenta administrativa que permite executar scripts SQL no banco.
 *    Ao vincular esta classe exclusivamente ao perfil 'dev', garantimos que a rota '/h2-console'
 *    só estará acessível no ambiente local de desenvolvimento, ficando desativada em Produção.
 *
 * ============================================================================================
 */

/*
 * PASSO 1: Declarar a classe como uma classe de configuração do Spring.
 * O Spring varre o projeto na inicialização e lê esta classe para registrar Beans.
 */
@Configuration

/*
 * PASSO 2: Restringir a execução desta classe apenas ao perfil "dev".
 * Se 'spring.profiles.active=dev' estiver configurado, o Spring carrega esta classe.
 * Se o perfil ativo for 'prod' ou outro, esta classe é completamente ignorada.
 */
@Profile("dev")
public class DevConfig {

    /*
     * PASSO 3: Definir o método de fábrica do Bean do Servlet.
     * A anotação @Bean indica que o retorno deste método deve ser gerenciado pelo container Spring.
     * Retornamos um 'ServletRegistrationBean', que é a estrutura do Spring Boot para registrar
     * Servlets diretamente no servidor web embutido (ex: Tomcat).
     */
    @Bean
    public ServletRegistrationBean<JakartaWebServlet> h2servletRegistration() {

        /*
         * PASSO 4: Instanciar o Servlet compatível com Jakarta EE e mapear o caminho URL.
         * - 'new JakartaWebServlet()': Instancia o Servlet moderno do H2.
         * - '"/h2-console/*"': Define o endpoint web onde o painel do H2 ficará acessível no navegador.
         */
        ServletRegistrationBean<JakartaWebServlet> registration =
                new ServletRegistrationBean<>(new JakartaWebServlet(), "/h2-console/*");

        /*
         * PASSO 5: Nomear o registro do Servlet.
         * Atribui um identificador interno ("H2Console") para o Servlet no contexto do Spring.
         */
        registration.setName("H2Console");

        /*
         * PASSO 6: Retornar o Bean configurado para ser registrado no container Web.
         */
        return registration;
    }
}

//http://localhost:8080/h2-console/
//mvnw clean spring-boot:run (alterações grandees varre todo lixo)
//mvnw spring-boot:run (no dia a dia)