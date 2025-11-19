package br.com.acheiacai.controller;

import br.com.acheiacai.dao.RelatorioDAO;
import br.com.acheiacai.model.relatorio.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@WebServlet("/relatorios/vendas")
public class RelatorioServlet extends HttpServlet {
    private final RelatorioDAO relatorioDAO = new RelatorioDAO();
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            LocalDate dataInicio = LocalDate.parse(request.getParameter("dataInicio"));
            LocalDate dataFim = LocalDate.parse(request.getParameter("dataFim"));

            List<ItemRelatorio> totaisComplementos = relatorioDAO.calcularTotalAdicionais(dataInicio, dataFim, "complementos");
            List<ItemRelatorio> totaisCoberturas = relatorioDAO.calcularTotalAdicionais(dataInicio, dataFim, "coberturas");
            List<VolumeProduto> volumesProduto = relatorioDAO.calcularVolumePorVariacao(dataInicio, dataFim);
            List<TotalPorPagamento> totaisPorPagamento = relatorioDAO.calcularTotaisPorPagamento(dataInicio, dataFim);
            List<ItemRelatorio> totaisOutrosProdutos = relatorioDAO.calcularTotaisOutrosProdutos(dataInicio, dataFim);

            // Cálculos Finais no Servlet
            BigDecimal totalGeral = totaisPorPagamento.stream()
                    .map(TotalPorPagamento::total)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            double totalAcai = volumesProduto.stream()
                    .filter(v -> "ACAI".equals(v.tipo()))
                    .mapToDouble(VolumeProduto::totalLitros)
                    .sum();

            double totalSorvete = volumesProduto.stream()
                    .filter(v -> "SORVETE".equals(v.tipo()))
                    .mapToDouble(VolumeProduto::totalLitros)
                    .sum();

            double totalSuco = volumesProduto.stream()
                    .filter(v -> "SUCO".equals(v.tipo()))
                    .mapToDouble(VolumeProduto::totalLitros)
                    .sum();

            double totalMousse = volumesProduto.stream()
                    .filter(v -> "MOUSSE".equals(v.tipo()))
                    .mapToDouble(VolumeProduto::totalLitros)
                    .sum();

            VolumeTotal volumesTotais = new VolumeTotal(totalAcai, totalSorvete, totalSuco, totalMousse);

            RelatorioVendas relatorio = new RelatorioVendas(totaisComplementos,
                                                            totaisCoberturas,
                                                            volumesProduto,
                                                            volumesTotais,
                                                            totaisOutrosProdutos,
                                                            totaisPorPagamento,
                                                            totalGeral);

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().print(objectMapper.writeValueAsString(relatorio));

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("{\"erro\":\"Falha ao gerar o relatório. Verifique os parâmetros de data.\"}");
            e.printStackTrace();
        }
    }
}