package com.diniz.lgeneral_learning;

/*
================================================================================
                    PLANO DE ESTUDOS: MÓDULO DE RECOVERY/RESET PASSWORD
================================================================================

Este plano segue uma abordagem Bottom-Up (da base até a superfície), que é a forma
mais eficiente de entender como a informação flui desde o banco de dados até a
interface REST da API.

--------------------------------------------------------------------------------
1. DIAGRAMA DE ARQUITETURA E FLUXO DE DADOS
--------------------------------------------------------------------------------

                      [ CLIENTE / FRONTEND ]
                           │          ▲
     1. POST /auth/forgot  │          │ 8. HTTP 204 No Content
        (ForgotPasswordDTO)│          │
                           │          │ 9. POST /auth/reset
                           │          │    (ResetPasswordDTO)
                           │          │
                           │          │ 16. HTTP 204 No Content
                           ▼          │
┌──────────────────────────────────────────────────────────────────────────────┐
│ CONTROLLER LAYER                                                             │
│  AuthenticationController                                                    │
└──────────────────────┬───────────────────────────────────────────────────────┘
                       │ 2. Chama createPasswordResetToken()
                       │ 10. Chama resetPassword()
                       ▼
┌──────────────────────────────────────────────────────────────────────────────┐
│ SERVICE LAYER (Orquestração & Regras de Negócio)                             │
│  PasswordResetService                                                        │
│                                                                              │
│  Dependências Injetadas:                                                     │
│   ├── PasswordEncoder (13. Encoda nova senha com BCrypt)                     │
│   └── EmailService    (7. Dispara e-mail com token para o usuário)           │
└───────────────┬──────────────────────────────────────────────┬───────────────┘
                │ 3. Busca User por e-mail                     │ 5. Instancia Token
                │ 4. Deleta token antigo se existir            │ 6. Salva Token
                │ 11. Busca e valida Token                     │ 14. Atualiza User
                │ 12. Deleta Token expirado/usado              │ 15. Salva User
                ▼                                              ▼
┌──────────────────────────────────────────────┐ ┌─────────────────────────────┐
│ REPOSITORY LAYER                             │ │ ENTITY LAYER                │
│  PasswordResetTokenRepository                │ │  PasswordResetToken         │
│  UserRepository                              │ │  User                       │
└───────────────────────┬──────────────────────┘ └─────────────────────────────┘
                        │
                        │ Persiste no Banco via JPA
                        ▼
               [ BANCO DE DADOS (JPA) ]


================================================================================
                    DETALHAMENTO DOS PASSO A PASSO (1 AO 16)
================================================================================

--- FLUXO 1: SOLICITAÇÃO DE RECUPERAÇÃO DE SENHA (FORGOT PASSWORD) ---

1. [CLIENTE -> AuthenticationController]
   O cliente envia uma requisição POST /auth/forgot-password transportando o
   payload de e-mail mapeado pela classe ForgotPasswordDTO.

2. [AuthenticationController -> PasswordResetService]
   A classe AuthenticationController recebe a requisição, valida o DTO com @Valid
   e repassa a chamada para o método createPasswordResetToken() da classe
   PasswordResetService.

3. [PasswordResetService -> UserRepository]
   A classe PasswordResetService executa o método findUserByEmail() da interface
   UserRepository para verificar se o e-mail informado pertence a um registro válido.

4. [PasswordResetService -> PasswordResetTokenRepository]
   A classe PasswordResetService executa o método findByUser() da interface
   PasswordResetTokenRepository. Se encontrar um token anterior ativo para o usuário,
   invoca o método delete() para removê-lo.

5. [PasswordResetService -> PasswordResetToken]
   A classe PasswordResetService gera uma String UUID aleatória, calcula a
   expiração para 30 minutos e instancia um novo objeto da classe de entidade
   PasswordResetToken, associando-o ao objeto da classe User.

6. [PasswordResetService -> PasswordResetTokenRepository -> BANCO]
   A classe PasswordResetService invoca o método save() da interface
   PasswordResetTokenRepository para persistir o novo objeto PasswordResetToken
   no banco de dados.

7. [PasswordResetService -> EmailService]
   A classe PasswordResetService invoca o método sendPasswordResetEmail() da
   classe EmailService repassando o endereço de e-mail do User e a String do token.

8. [AuthenticationController -> CLIENTE]
   A classe AuthenticationController finaliza o método retornando uma resposta
   ResponseEntity.noContent() (HTTP 204 No Content) ao cliente.


--- FLUXO 2: REDEFINIÇÃO EFETIVA DA SENHA (RESET PASSWORD) ---

9. [CLIENTE -> AuthenticationController]
   O cliente envia a requisição POST /auth/reset-password transportando o token
   e a nova senha mapeados pela classe ResetPasswordDTO.

10. [AuthenticationController -> PasswordResetService]
    A classe AuthenticationController valida o payload e invoca o método
    resetPassword() da classe PasswordResetService.

11. [PasswordResetService -> PasswordResetTokenRepository & PasswordResetToken]
    A classe PasswordResetService executa o método findByToken() da interface
    PasswordResetTokenRepository para recuperar o token do banco. Em seguida,
    invoca o método isExpired() diretamente na entidade PasswordResetToken.

12. [PasswordResetService -> PasswordResetTokenRepository] (Caso expirado)
    Se o método isExpired() retornar true, a classe PasswordResetService executa
    o método delete() do PasswordResetTokenRepository e lança uma exceção.

13. [PasswordResetService -> PasswordEncoder]
    A classe PasswordResetService invoca o método encode() do componente
    PasswordEncoder (BCrypt) para gerar o hash seguro da nova senha recebida.

14. [PasswordResetService -> User]
    A classe PasswordResetService recupera a entidade User associada ao token e
    invoca o método setPassword() na classe User atualizando o hash da senha.

15. [PasswordResetService -> UserRepository & PasswordResetTokenRepository]
    A classe PasswordResetService executa o método save() do UserRepository para
    persistir o User atualizado. Logo em seguida, executa o método delete() do
    PasswordResetTokenRepository para deletar o PasswordResetToken (uso único).

16. [AuthenticationController -> CLIENTE]
    A classe AuthenticationController finaliza a requisição retornando a resposta
    ResponseEntity.noContent() (HTTP 204 No Content), confirmando a alteração de senha.

--------------------------------------------------------------------------------
2. SEQUÊNCIA DE ESTUDO PASSO A PASSO
--------------------------------------------------------------------------------

PASSO 1: Camada de Domínio/Persistência (Entities)
--------------------------------------------------
📄 Classes:
   - com.diniz.springbootstudy.entities.PasswordResetToken
   - com.diniz.springbootstudy.entities.User
🎯 O que estudar nestas classes:
   - Mapeamento JPA (@Entity, @Table, @OneToOne, @JoinColumn).
   - Como PasswordResetToken se relaciona diretamente com a entidade User.
   - O método de utilidade do próprio modelo de domínio: isExpired() na classe
     PasswordResetToken. Entenda como a regra de checagem de expiração fica
     encapsulada na própria entidade.

PASSO 2: Camada de Comunicação com o Banco (Repositories)
---------------------------------------------------------
📄 Classes:
   - com.diniz.springbootstudy.repositories.PasswordResetTokenRepository
   - com.diniz.springbootstudy.repositories.UserRepository
🎯 O que estudar nestas interfaces:
   - Herança de JpaRepository e criação de Derived Query Methods.
   - Entenda no PasswordResetTokenRepository a utilidade de:
       a) findByToken(String token): busca rápida pelo hash do token.
       b) findByUser(User user): limpa tokens antigos antes de gerar um novo.
   - Entenda no UserRepository a utilidade de:
       a) findUserByEmail(String email): validação de existência do e-mail.

PASSO 3: Camada de Transferência de Dados (DTOs)
------------------------------------------------
📄 Classes:
   - com.diniz.springbootstudy.dto.ForgotPasswordDTO
   - com.diniz.springbootstudy.dto.ResetPasswordDTO
🎯 O que estudar nestas classes:
   - Uso da funcionalidade record do Java para criar objetos imutáveis.
   - Anotações do Jakarta Bean Validation (@NotBlank, @Email, @Size).
   - Como eles servem de "contrato" garantindo que os dados cheguem válidos no backend.

PASSO 4: Camada de Regras de Negócio e Serviços (Services & Security)
---------------------------------------------------------------------
📄 Classes:
   - com.diniz.springbootstudy.services.security.PasswordResetService
   - com.diniz.springbootstudy.services.email.EmailService
   - org.springframework.security.crypto.password.PasswordEncoder
🎯 O que estudar nestas classes:
   - Na classe PasswordResetService, entenda os dois fluxos principais:
       a) createPasswordResetToken(ForgotPasswordDTO dto):
          1. Busca o usuário por e-mail via UserRepository.
          2. Limpa token existente anterior via PasswordResetTokenRepository.
          3. Gera novo UUID e calcula data de expiração (30 min).
          4. Salva no banco e aciona o EmailService.
       b) resetPassword(ResetPasswordDTO dto):
          1. Busca o token no banco.
          2. Valida se está expirado com isExpired() (se sim, deleta e lança exceção).
          3. Atualiza a senha do usuário criptografada com BCrypt via PasswordEncoder.
          4. Remove o token após o uso (política de uso único).
   - O papel do EmailService em abstrair o envio do e-mail com o token.
   - Uso da anotação @Transactional para garantir consistência nas operações.

PASSO 5: Camada de Entrada HTTP (Controller)
--------------------------------------------
📄 Classe:
   - com.diniz.springbootstudy.controllers.AuthenticationController
🎯 O que estudar nesta classe:
   - Anotações do Spring MVC (@RestController, @RequestMapping, @PostMapping, @RequestBody).
   - Uso de @Valid para disparar as validações dos DTOs automaticamente.
   - Mapeamento das rotas /auth/forgot-password e /auth/reset-password.
   - Retorno com ResponseEntity.noContent() (HTTP 204) nos endpoints de senha,
     sinalizando execução bem-sucedida sem necessidade de corpo na resposta.

================================================================================
*/
public class ForgotPasswordStudingPlan {
}