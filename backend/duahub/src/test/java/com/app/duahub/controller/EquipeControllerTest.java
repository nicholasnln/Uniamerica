package com.app.duahub.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.app.duahub.entity.Equipe;
import com.app.duahub.entity.Participante;
import com.app.duahub.service.EquipeService;
import com.app.duahub.dto.EquipeDTO;

@SpringBootTest
public class EquipeControllerTest {

    private EquipeDTO equipeDTO;

	@Autowired
	EquipeController equipeController;
	
	@MockBean
	EquipeService equipeService;
	
	private Equipe equipe;
    private Participante participante1;
    private Participante participante2;
    private List<Equipe> listaEquipes;
    private List<Equipe> listaVazia;

    @BeforeEach
    void setup() {
        participante1 = new Participante();
        participante1.setId(1L);
        participante1.setNome("João Silva");

        participante2 = new Participante();
        participante2.setId(2L);
        participante2.setNome("Maria Souza");

        equipe = new Equipe();
        equipe.setId(1L);
        equipe.setNome("Equipe A");
        equipe.setParticipantes(List.of(participante1, participante2));

        equipeDTO = new EquipeDTO();
        equipeDTO.setNome("Equipe A");
        equipeDTO.setParticipantesIds(List.of(1L, 2L));

        listaEquipes = List.of(equipe);
        listaVazia = new ArrayList<>();
    }



    @Test
    void cenario01_retornaListaVaziaDeEquipes() {
        when(equipeService.findAll()).thenReturn(listaVazia);

        ResponseEntity<List<Equipe>> retorno = equipeController.findAll();

        assertEquals(HttpStatus.OK, retorno.getStatusCode());
        assertEquals(0, retorno.getBody().size());
    }

    @Test
    void cenario02_retornaListaDeEquipesComParticipantes() {
        when(equipeService.findAll()).thenReturn(listaEquipes);

        ResponseEntity<List<Equipe>> retorno = equipeController.findAll();

        assertEquals(HttpStatus.OK, retorno.getStatusCode());
        assertEquals(1, retorno.getBody().size());
        assertEquals("Equipe A", retorno.getBody().get(0).getNome());
        assertEquals(2, retorno.getBody().get(0).getParticipantes().size());
    }

    @Test
    void cenario03_salvarEquipeComSucesso() {
        when(equipeService.save(equipe)).thenReturn("Equipe criada com sucesso!");

        ResponseEntity<EquipeDTO> retorno = equipeController.save(equipeDTO);

        assertEquals(HttpStatus.CREATED, retorno.getStatusCode());
        assertEquals("Equipe criada com sucesso!", retorno.getBody());
    }

    @Test
    void cenario02_salvarEquipeErro() {
        Equipe equipe = new Equipe();  // Simulando uma equipe
        when(equipeService.save(equipe)).thenThrow(new RuntimeException("Erro ao salvar equipe"));

        ResponseEntity<EquipeDTO> retorno = equipeController.save(equipeDTO);

        assertEquals(400, retorno.getStatusCodeValue());  // Status HTTP 400 (Bad Request)
        assertEquals("Erro ao criar equipe: Erro ao salvar equipe", retorno.getBody());
    }

    @Test
    void cenario05_atualizarEquipeComSucesso() {
        equipe.setNome("Equipe Atualizada");
        when(equipeService.update(equipe, 1L)).thenReturn("Equipe atualizada com sucesso!");

        ResponseEntity<String> retorno = equipeController.update(equipe, 1L);

        assertEquals(HttpStatus.OK, retorno.getStatusCode());
        assertEquals("Equipe atualizada com sucesso!", retorno.getBody());
    }

    @Test
    void cenario04_atualizarEquipeErro() {
        Equipe equipe = new Equipe();  // Simulando uma equipe
        when(equipeService.update(equipe, 1L)).thenThrow(new RuntimeException("Erro ao atualizar equipe"));

        ResponseEntity<String> retorno = equipeController.update(equipe, 1L);

        assertEquals(400, retorno.getStatusCodeValue());  // Status HTTP 400 (Bad Request)
        assertEquals("Erro ao atualizar equipe: Erro ao atualizar equipe", retorno.getBody());
    }

    @Test
    void cenario07_deletarEquipeComSucesso() {
        when(equipeService.delete(1L)).thenReturn("Equipe deletada com sucesso!");

        ResponseEntity<String> retorno = equipeController.delete(1L);

        assertEquals(HttpStatus.OK, retorno.getStatusCode());
        assertEquals("Equipe deletada com sucesso!", retorno.getBody());
    }

    @Test
    void cenario06_deletarEquipeErro() {
        when(equipeService.delete(1L)).thenThrow(new RuntimeException("Erro ao deletar equipe"));

        ResponseEntity<String> retorno = equipeController.delete(1L);

        assertEquals(400, retorno.getStatusCodeValue());  // Status HTTP 400 (Bad Request)
        assertEquals("Erro ao deletar equipe: Erro ao deletar equipe", retorno.getBody());
    }

    @Test
    void cenario09_buscarEquipePorIdComSucesso() {
        // Inicializando um objeto 'equipe' para o mock
        Equipe equipe = new Equipe();
        equipe.setId(1L);
        equipe.setNome("Equipe A");

        // Mockando a resposta do serviço
        when(equipeService.findById(1L)).thenReturn(equipe);

        // Chamando o método da controller
        ResponseEntity<Object> retorno = equipeController.findById(1L);  // Ajuste para ResponseEntity<Object>

        // Verificando o código de status e o corpo da resposta
        assertEquals(HttpStatus.OK, retorno.getStatusCode());  // Verifica se o status é 200 (OK)
        
        // O corpo da resposta deve ser do tipo Equipe, então precisamos fazer o cast
        Equipe equipeRetornada = (Equipe) retorno.getBody();
        
        assertEquals("Equipe A", equipeRetornada.getNome());  // Verifica se o nome da equipe é o esperado
    }

    @Test
    void cenario10_buscarEquipePorIdNaoEncontrada() {
        // Mockando o comportamento do serviço para lançar uma exceção
        doThrow(new RuntimeException("Equipe não encontrada")).when(equipeService).findById(1L);

        // Chamando o método da controller
        ResponseEntity<Object> retorno = equipeController.findById(1L);

        // Verificando o código de status e o corpo da resposta
        assertEquals(HttpStatus.BAD_REQUEST, retorno.getStatusCode());  // Verifica se o status é 400 (BAD REQUEST)
        assertEquals("Equipe não encontrada", retorno.getBody());  // Verifica se a mensagem no corpo é a esperada
    }
}
