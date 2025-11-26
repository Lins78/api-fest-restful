package com.exemplo.apifest.unit.service;

import com.exemplo.apifest.dto.LoginDTO;
import com.exemplo.apifest.dto.RegisterDTO;
import com.exemplo.apifest.dto.response.AuthResponseDTO;
import com.exemplo.apifest.exception.BusinessException;
import com.exemplo.apifest.exception.UnauthorizedException;
import com.exemplo.apifest.model.User;
import com.exemplo.apifest.model.Role;
import com.exemplo.apifest.repository.UserRepository;
import com.exemplo.apifest.service.impl.AuthServiceImpl;
// import com.exemplo.apifest.util.JwtUtil; // Classe movida para security package
import com.exemplo.apifest.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários avançados para AuthService - Roteiro 9.
 * 
 * CENÁRIOS COMPLEXOS TESTADOS:
 * - Autenticação JWT com diferentes tipos de usuário
 * - Registro com validações de segurança
 * - Controle de acesso baseado em roles
 * - Validação de tokens e refresh
 * - Cenários de falha de autenticação
 * - Políticas de senha segura
 * 
 * TÉCNICAS AVANÇADAS:
 * - Mock de componentes de segurança
 * - Simulação de cenários de ataques
 * - Validação de payloads JWT
 * - Teste de expiração de tokens
 * - Verificação de encoding de senhas
 * 
 * @author DeliveryTech Team
 * @version 1.0 - Roteiro 9
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("🔐 AuthService - Testes Unitários Avançados")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthServiceImpl authService;

    private User usuarioExistente;
    private LoginDTO loginDTOValido;
    private RegisterDTO registerDTOValido;

    @BeforeEach
    void setUp() {
        usuarioExistente = new User();
        usuarioExistente.setId(1L);
        usuarioExistente.setNome("João Silva");
        usuarioExistente.setEmail("joao.silva@email.com");
        usuarioExistente.setSenha("$2a$10$encodedPassword");
        usuarioExistente.setTelefone("(11) 99999-9999");
        usuarioExistente.setRole(Role.CLIENTE);

        loginDTOValido = new LoginDTO();
        loginDTOValido.setEmail("joao.silva@email.com");
        loginDTOValido.setSenha("senhaSegura123");

        registerDTOValido = new RegisterDTO();
        registerDTOValido.setNome("Maria Oliveira");
        registerDTOValido.setEmail("maria.oliveira@email.com");
        registerDTOValido.setSenha("senhaSegura123");
        registerDTOValido.setTelefone("(11) 88888-8888");
    }

    // ========== TESTES DE AUTENTICAÇÃO ==========

    @Nested
    @DisplayName("Autenticação de Usuários")
    class AutenticacaoUsuarios {

        @Test
        @DisplayName("✅ Deve autenticar usuário com credenciais válidas")
        void deveAutenticarUsuarioComCredenciaisValidas() {
            // Given
            when(userRepository.findByEmail(loginDTOValido.getEmail()))
                    .thenReturn(Optional.of(usuarioExistente));
            when(passwordEncoder.matches(loginDTOValido.getSenha(), usuarioExistente.getSenha()))
                    .thenReturn(true);
            when(jwtUtil.generateToken(usuarioExistente))
                    .thenReturn("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...");

            // When
            AuthResponseDTO resultado = authService.login(loginDTOValido);

            // Then
            assertThat(resultado).isNotNull();
            assertThat(resultado.getToken()).isNotBlank();
            assertThat(resultado.getUser().getEmail()).isEqualTo(usuarioExistente.getEmail());
            assertThat(resultado.getUser().getRole()).isEqualTo(usuarioExistente.getRole());

            verify(userRepository).findByEmail(loginDTOValido.getEmail());
            verify(passwordEncoder).matches(loginDTOValido.getSenha(), usuarioExistente.getSenha());
            verify(jwtUtil).generateToken(usuarioExistente);
        }

        @Test
        @DisplayName("❌ Deve falhar com email inexistente")
        void deveFalharComEmailInexistente() {
            // Given
            when(userRepository.findByEmail(loginDTOValido.getEmail()))
                    .thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> authService.login(loginDTOValido))
                    .isInstanceOf(UnauthorizedException.class)
                    .hasMessageContaining("Credenciais inválidas");

            verify(passwordEncoder, never()).matches(anyString(), anyString());
            verify(jwtUtil, never()).generateToken(any(User.class));
        }

        @Test
        @DisplayName("❌ Deve falhar com senha incorreta")
        void deveFalharComSenhaIncorreta() {
            // Given
            when(userRepository.findByEmail(loginDTOValido.getEmail()))
                    .thenReturn(Optional.of(usuarioExistente));
            when(passwordEncoder.matches(loginDTOValido.getSenha(), usuarioExistente.getSenha()))
                    .thenReturn(false);

            // When & Then
            assertThatThrownBy(() -> authService.login(loginDTOValido))
                    .isInstanceOf(UnauthorizedException.class)
                    .hasMessageContaining("Credenciais inválidas");

            verify(jwtUtil, never()).generateToken(any(User.class));
        }

        @Test
        @DisplayName("✅ Deve autenticar usuário ADMIN")
        void deveAutenticarUsuarioAdmin() {
            // Given
            usuarioExistente.setRole(Role.ADMIN);
            when(userRepository.findByEmail(loginDTOValido.getEmail()))
                    .thenReturn(Optional.of(usuarioExistente));
            when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
            when(jwtUtil.generateToken(usuarioExistente)).thenReturn("admin.token.here");

            // When
            AuthResponseDTO resultado = authService.login(loginDTOValido);

            // Then
            assertThat(resultado.getUser().getRole()).isEqualTo(Role.ADMIN);
            assertThat(resultado.getToken()).isEqualTo("admin.token.here");
        }

        @Test
        @DisplayName("✅ Deve validar token válido")
        void deveValidarTokenValido() {
            // Given
            String token = "valid.jwt.token";
            when(jwtUtil.validateToken(token)).thenReturn(true);
            when(jwtUtil.extractEmail(token)).thenReturn(usuarioExistente.getEmail());
            when(userRepository.findByEmail(usuarioExistente.getEmail()))
                    .thenReturn(Optional.of(usuarioExistente));

            // When
            boolean isValid = authService.validateToken(token);

            // Then
            assertThat(isValid).isTrue();
            verify(jwtUtil).validateToken(token);
            verify(jwtUtil).extractEmail(token);
        }

        @Test
        @DisplayName("❌ Deve invalidar token expirado")
        void deveInvalidarTokenExpirado() {
            // Given
            String tokenExpirado = "expired.jwt.token";
            when(jwtUtil.validateToken(tokenExpirado)).thenReturn(false);

            // When
            boolean isValid = authService.validateToken(tokenExpirado);

            // Then
            assertThat(isValid).isFalse();
            verify(jwtUtil, never()).extractEmail(anyString());
        }
    }

    // ========== TESTES DE REGISTRO ==========

    @Nested
    @DisplayName("Registro de Usuários")
    class RegistroUsuarios {

        @Test
        @DisplayName("✅ Deve registrar novo usuário com dados válidos")
        void deveRegistrarNovoUsuarioComDadosValidos() {
            // Given
            when(userRepository.existsByEmail(registerDTOValido.getEmail())).thenReturn(false);
            when(passwordEncoder.encode(registerDTOValido.getSenha()))
                    .thenReturn("$2a$10$newEncodedPassword");
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                User user = invocation.getArgument(0);
                user.setId(2L);
                return user;
            });
            when(jwtUtil.generateToken(any(User.class)))
                    .thenReturn("new.user.token");

            // When
            AuthResponseDTO resultado = authService.register(registerDTOValido);

            // Then
            assertThat(resultado).isNotNull();
            assertThat(resultado.getToken()).isEqualTo("new.user.token");
            assertThat(resultado.getUser().getEmail()).isEqualTo(registerDTOValido.getEmail());
            assertThat(resultado.getUser().getRole()).isEqualTo(Role.CLIENTE);

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());
            
            User userSalvo = userCaptor.getValue();
            assertThat(userSalvo.getNome()).isEqualTo(registerDTOValido.getNome());
            assertThat(userSalvo.getSenha()).isEqualTo("$2a$10$newEncodedPassword");
        }

        @Test
        @DisplayName("❌ Deve falhar com email já cadastrado")
        void deveFalharComEmailJaCadastrado() {
            // Given
            when(userRepository.existsByEmail(registerDTOValido.getEmail())).thenReturn(true);

            // When & Then
            assertThatThrownBy(() -> authService.register(registerDTOValido))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("já está cadastrado");

            verify(passwordEncoder, never()).encode(anyString());
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("❌ Deve falhar com senha fraca")
        void deveFalharComSenhaFraca() {
            // Given
            registerDTOValido.setSenha("123");

            // When & Then
            assertThatThrownBy(() -> authService.register(registerDTOValido))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Senha deve ter pelo menos 8 caracteres");
        }

        @Test
        @DisplayName("❌ Deve falhar com email inválido")
        void deveFalharComEmailInvalido() {
            // Given
            registerDTOValido.setEmail("email-invalido");

            // When & Then
            assertThatThrownBy(() -> authService.register(registerDTOValido))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Email inválido");
        }

        @Test
        @DisplayName("❌ Deve falhar com telefone inválido")
        void deveFalharComTelefoneInvalido() {
            // Given
            registerDTOValido.setTelefone("123456");

            // When & Then
            assertThatThrownBy(() -> authService.register(registerDTOValido))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Telefone inválido");
        }

        @Test
        @DisplayName("✅ Deve definir role padrão como USER")
        void deveDefinirRolePadraoComoUser() {
            // Given
            when(userRepository.existsByEmail(any())).thenReturn(false);
            when(passwordEncoder.encode(any())).thenReturn("encoded");
            
            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            when(userRepository.save(userCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));
            when(jwtUtil.generateToken(any(User.class))).thenReturn("token");

            // When
            authService.register(registerDTOValido);

            // Then
            User userSalvo = userCaptor.getValue();
            assertThat(userSalvo.getRole()).isEqualTo(Role.CLIENTE);
        }
    }

    // ========== TESTES DE REFRESH TOKEN ==========

    @Nested
    @DisplayName("Refresh Token")
    class RefreshToken {

        @Test
        @DisplayName("✅ Deve renovar token válido")
        void deveRenovarTokenValido() {
            // Given
            String tokenAtual = "current.jwt.token";
            String novoToken = "new.jwt.token";
            
            when(jwtUtil.validateToken(tokenAtual)).thenReturn(true);
            when(jwtUtil.extractEmail(tokenAtual)).thenReturn(usuarioExistente.getEmail());
            when(userRepository.findByEmail(usuarioExistente.getEmail()))
                    .thenReturn(Optional.of(usuarioExistente));
            when(jwtUtil.generateToken(usuarioExistente)).thenReturn(novoToken);

            // When
            AuthResponseDTO resultado = authService.refreshToken(tokenAtual);

            // Then
            assertThat(resultado).isNotNull();
            assertThat(resultado.getToken()).isEqualTo(novoToken);
            assertThat(resultado.getUser().getEmail()).isEqualTo(usuarioExistente.getEmail());
        }

        @Test
        @DisplayName("❌ Deve falhar com token inválido para refresh")
        void deveFalharComTokenInvalidoParaRefresh() {
            // Given
            String tokenInvalido = "invalid.jwt.token";
            when(jwtUtil.validateToken(tokenInvalido)).thenReturn(false);

            // When & Then
            assertThatThrownBy(() -> authService.refreshToken(tokenInvalido))
                    .isInstanceOf(UnauthorizedException.class)
                    .hasMessageContaining("Token inválido");

            verify(jwtUtil, never()).extractEmail(anyString());
            verify(jwtUtil, never()).generateToken(any(User.class));
        }
    }

    // ========== TESTES DE AUTORIZAÇÃO ==========

    @Nested
    @DisplayName("Autorização e Controle de Acesso")
    class AutorizacaoControleAcesso {

        @Test
        @DisplayName("✅ Deve permitir acesso de usuário ADMIN")
        void devePermitirAcessoUsuarioAdmin() {
            // Given
            usuarioExistente.setRole(Role.ADMIN);

            // When
            boolean temPermissao = authService.hasAdminAccess(usuarioExistente);

            // Then
            assertThat(temPermissao).isTrue();
        }

        @Test
        @DisplayName("❌ Deve negar acesso de usuário comum a área admin")
        void deveNegarAcessoUsuarioComumAreaAdmin() {
            // Given
            usuarioExistente.setRole(Role.CLIENTE);

            // When
            boolean temPermissao = authService.hasAdminAccess(usuarioExistente);

            // Then
            assertThat(temPermissao).isFalse();
        }

        @Test
        @DisplayName("✅ Deve validar se usuário pode acessar próprio recurso")
        void deveValidarSeUsuarioPodeAcessarProprioRecurso() {
            // Given
            Long recursoId = 1L;
            usuarioExistente.setId(1L);

            // When
            boolean podeAcessar = authService.canAccessOwnResource(usuarioExistente, recursoId);

            // Then
            assertThat(podeAcessar).isTrue();
        }

        @Test
        @DisplayName("❌ Deve impedir acesso a recurso de outro usuário")
        void deveImpedirAcessoRecursoOutroUsuario() {
            // Given
            Long recursoId = 2L;
            usuarioExistente.setId(1L);

            // When
            boolean podeAcessar = authService.canAccessOwnResource(usuarioExistente, recursoId);

            // Then
            assertThat(podeAcessar).isFalse();
        }
    }

    // ========== TESTES DE SEGURANÇA ==========

    @Nested
    @DisplayName("Validações de Segurança")
    class ValidacoesSeguranca {

        @Test
        @DisplayName("❌ Deve falhar com dados nulos")
        void deveFalharComDadosNulos() {
            // When & Then
            assertThatThrownBy(() -> authService.login(null))
                    .isInstanceOf(IllegalArgumentException.class);
                    
            assertThatThrownBy(() -> authService.register(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("✅ Deve validar política de senha segura")
        void deveValidarPoliticaSenhaSegura() {
            // Given - Senha sem caracteres especiais
            registerDTOValido.setSenha("senhasimples");

            // When & Then
            assertThatThrownBy(() -> authService.register(registerDTOValido))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Senha deve conter pelo menos um número");
        }

        @Test
        @DisplayName("✅ Deve validar tamanho mínimo do nome")
        void deveValidarTamanhoMinimoNome() {
            // Given
            registerDTOValido.setNome("Ab");

            // When & Then
            assertThatThrownBy(() -> authService.register(registerDTOValido))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Nome deve ter pelo menos 3 caracteres");
        }

        @Test
        @DisplayName("❌ Deve falhar com tentativa de SQL Injection no email")
        void deveFalharComTentativaSqlInjectionEmail() {
            // Given
            loginDTOValido.setEmail("'; DROP TABLE users; --");

            // When & Then
            assertThatThrownBy(() -> authService.login(loginDTOValido))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Email inválido");
        }

        @Test
        @DisplayName("❌ Deve limitar tentativas de login consecutivas")
        void deveLimitarTentativasLoginConsecutivas() {
            // Given
            String email = "usuario@teste.com";
            
            // Simular 5 tentativas falhadas
            for (int i = 0; i < 5; i++) {
                when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
                
                assertThatThrownBy(() -> {
                    LoginDTO login = new LoginDTO();
                    login.setEmail(email);
                    login.setSenha("senhaErrada");
                    authService.login(login);
                }).isInstanceOf(UnauthorizedException.class);
            }

            // When & Then - 6ª tentativa deve ser bloqueada
            assertThatThrownBy(() -> {
                LoginDTO login = new LoginDTO();
                login.setEmail(email);
                login.setSenha("senhaCorreta");
                authService.login(login);
            }).isInstanceOf(BusinessException.class)
              .hasMessageContaining("Muitas tentativas de login");
        }
    }
}