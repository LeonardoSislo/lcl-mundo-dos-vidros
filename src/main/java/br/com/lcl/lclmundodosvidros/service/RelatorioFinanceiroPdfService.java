package br.com.lcl.lclmundodosvidros.service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.openpdf.text.Document;
import org.openpdf.text.Element;
import org.openpdf.text.Font;
import org.openpdf.text.FontFactory;
import org.openpdf.text.PageSize;
import org.openpdf.text.Paragraph;
import org.openpdf.text.Phrase;
import org.openpdf.text.Rectangle;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import br.com.lcl.lclmundodosvidros.dto.ResumoFinanceiro;
import br.com.lcl.lclmundodosvidros.model.Servico;

@Service
public class RelatorioFinanceiroPdfService {

    private static final Color COR_PRINCIPAL =
            new Color(53, 194, 193);

    private static final Color COR_CABECALHO =
            new Color(241, 245, 249);

    private static final Color COR_TEXTO =
            new Color(23, 32, 51);

    private static final Color COR_VERDE =
            new Color(22, 101, 52);

    private static final Color COR_VERMELHO =
            new Color(185, 28, 28);

    private static final DateTimeFormatter FORMATO_DATA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final DateTimeFormatter FORMATO_EMISSAO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private static final Locale LOCALE_BRASIL =
        new Locale("pt", "BR");

private static final NumberFormat FORMATO_MOEDA =
        NumberFormat.getCurrencyInstance(LOCALE_BRASIL);


    public byte[] gerarPdf(
            ResumoFinanceiro resumo,
            LocalDate dataInicial,
            LocalDate dataFinal,
            String nomeTipo,
            String nomeCliente) {

        ByteArrayOutputStream saida =
                new ByteArrayOutputStream();

        Document documento = new Document(
                PageSize.A4.rotate(),
                28,
                28,
                28,
                28
        );

        try {
            PdfWriter.getInstance(documento, saida);
            documento.open();

            adicionarCabecalho(documento);
            adicionarPeriodo(
                    documento,
                    dataInicial,
                    dataFinal,
                    nomeTipo,
                    nomeCliente
            );
            adicionarTotais(documento, resumo);
            adicionarTabela(documento, resumo);
            adicionarRodape(documento);

        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Não foi possível gerar o relatório PDF.",
                    exception
            );
        } finally {
            documento.close();
        }

        return saida.toByteArray();
    }

    private void adicionarCabecalho(Document documento)
            throws Exception {

        Font fonteTitulo = criarFonte(
                19,
                Font.BOLD,
                COR_TEXTO
        );

        Font fonteSubtitulo = criarFonte(
                10,
                Font.NORMAL,
                Color.GRAY
        );

        PdfPTable cabecalho = new PdfPTable(
                new float[] { 3f, 1f }
        );

        cabecalho.setWidthPercentage(100);

        PdfPCell titulo = new PdfPCell();
        titulo.setBorder(Rectangle.NO_BORDER);
        titulo.setPaddingBottom(12);

        titulo.addElement(
                new Paragraph(
                        "LCL Mundo dos Vidros",
                        fonteTitulo
                )
        );

        titulo.addElement(
                new Paragraph(
                        "Resumo financeiro de serviços executados",
                        fonteSubtitulo
                )
        );

        PdfPCell emissao = new PdfPCell(
                new Phrase(
                        "Emitido em\n"
                                + LocalDateTime.now()
                                        .format(FORMATO_EMISSAO),
                        fonteSubtitulo
                )
        );

        emissao.setBorder(Rectangle.NO_BORDER);
        emissao.setHorizontalAlignment(Element.ALIGN_RIGHT);
        emissao.setVerticalAlignment(Element.ALIGN_MIDDLE);

        cabecalho.addCell(titulo);
        cabecalho.addCell(emissao);

        documento.add(cabecalho);

        PdfPTable linha = new PdfPTable(1);
        linha.setWidthPercentage(100);

        PdfPCell celulaLinha = new PdfPCell();
        celulaLinha.setFixedHeight(2);
        celulaLinha.setBackgroundColor(COR_PRINCIPAL);
        celulaLinha.setBorder(Rectangle.NO_BORDER);

        linha.addCell(celulaLinha);
        documento.add(linha);

        documento.add(new Paragraph(" "));
    }

    private void adicionarPeriodo(
            Document documento,
            LocalDate dataInicial,
            LocalDate dataFinal,
            String nomeTipo,
            String nomeCliente) throws Exception {

        PdfPTable tabela = new PdfPTable(4);
        tabela.setWidthPercentage(100);
        tabela.setSpacingAfter(14);

        adicionarInformacao(
                tabela,
                "Data inicial",
                formatarData(dataInicial)
        );

        adicionarInformacao(
                tabela,
                "Data final",
                formatarData(dataFinal)
        );

        adicionarInformacao(
                tabela,
                "Tipo de serviço",
                textoOuPadrao(nomeTipo, "Todos os tipos")
        );

        adicionarInformacao(
                tabela,
                "Cliente",
                textoOuPadrao(nomeCliente, "Todos os clientes")
        );

        documento.add(tabela);
    }

    private void adicionarTotais(
            Document documento,
            ResumoFinanceiro resumo) throws Exception {

        PdfPTable tabela = new PdfPTable(4);
        tabela.setWidthPercentage(100);
        tabela.setSpacingAfter(18);

        adicionarTotal(
                tabela,
                "Serviços executados",
                String.valueOf(resumo.getQuantidadeServicos()),
                COR_TEXTO
        );

        adicionarTotal(
                tabela,
                "Total de entradas",
                formatarMoeda(resumo.getTotalEntradas()),
                COR_VERDE
        );

        adicionarTotal(
                tabela,
                "Total de saídas",
                formatarMoeda(resumo.getTotalSaidas()),
                COR_VERMELHO
        );

        Color corResultado =
                resumo.getLucro().signum() >= 0
                        ? COR_PRINCIPAL
                        : COR_VERMELHO;

        adicionarTotal(
                tabela,
                "Lucro / prejuízo",
                formatarMoeda(resumo.getLucro()),
                corResultado
        );

        documento.add(tabela);
    }

    private void adicionarTabela(
            Document documento,
            ResumoFinanceiro resumo) throws Exception {

        Font tituloTabela = criarFonte(
                14,
                Font.BOLD,
                COR_TEXTO
        );

        Paragraph titulo = new Paragraph(
                "Extrato financeiro",
                tituloTabela
        );

        titulo.setSpacingAfter(10);
        documento.add(titulo);

        if (resumo.getServicos().isEmpty()) {
            Paragraph vazio = new Paragraph(
                    "Nenhum serviço concluído foi encontrado "
                            + "no período informado.",
                    criarFonte(10, Font.NORMAL, Color.GRAY)
            );

            vazio.setAlignment(Element.ALIGN_CENTER);
            documento.add(vazio);

            return;
        }

        PdfPTable tabela = new PdfPTable(
                new float[] {
                    1.1f,
                    0.6f,
                    1.7f,
                    1.3f,
                    2.6f,
                    1.2f,
                    1.2f,
                    1.2f
                }
        );

        tabela.setWidthPercentage(100);
        tabela.setHeaderRows(1);

        adicionarCabecalhoTabela(tabela, "Data");
        adicionarCabecalhoTabela(tabela, "ID");
        adicionarCabecalhoTabela(tabela, "Cliente");
        adicionarCabecalhoTabela(tabela, "Tipo");
        adicionarCabecalhoTabela(tabela, "Descrição");
        adicionarCabecalhoTabela(tabela, "Entrada");
        adicionarCabecalhoTabela(tabela, "Saída");
        adicionarCabecalhoTabela(tabela, "Lucro");

        for (Servico servico : resumo.getServicos()) {
            BigDecimal entrada = valorOuZero(
                    servico.getValorCobrado()
            );

            BigDecimal saida = valorOuZero(
                    servico.getValorCusto()
            );

            BigDecimal lucro = entrada.subtract(saida);

            adicionarCelula(
                    tabela,
                    formatarData(servico.getDataInstalacao()),
                    Element.ALIGN_LEFT,
                    COR_TEXTO
            );

            adicionarCelula(
                    tabela,
                    String.valueOf(servico.getIdServico()),
                    Element.ALIGN_CENTER,
                    COR_TEXTO
            );

            adicionarCelula(
                    tabela,
                    servico.getCliente() != null
                            ? servico.getCliente().getNome()
                            : "-",
                    Element.ALIGN_LEFT,
                    COR_TEXTO
            );

            adicionarCelula(
                    tabela,
                    servico.getTipoDeServico() != null
                            ? servico.getTipoDeServico().getTipo()
                            : "-",
                    Element.ALIGN_LEFT,
                    COR_TEXTO
            );

            adicionarCelula(
                    tabela,
                    textoOuPadrao(
                            servico.getDescricao(),
                            "-"
                    ),
                    Element.ALIGN_LEFT,
                    COR_TEXTO
            );

            adicionarCelula(
                    tabela,
                    formatarMoeda(entrada),
                    Element.ALIGN_RIGHT,
                    COR_VERDE
            );

            adicionarCelula(
                    tabela,
                    formatarMoeda(saida),
                    Element.ALIGN_RIGHT,
                    COR_VERMELHO
            );

            adicionarCelula(
                    tabela,
                    formatarMoeda(lucro),
                    Element.ALIGN_RIGHT,
                    lucro.signum() >= 0
                            ? COR_VERDE
                            : COR_VERMELHO
            );
        }

        documento.add(tabela);
    }

    private void adicionarRodape(Document documento)
            throws Exception {

        Paragraph rodape = new Paragraph(
                "Relatório gerado pelo sistema "
                        + "LCL Mundo dos Vidros.",
                criarFonte(8, Font.NORMAL, Color.GRAY)
        );

        rodape.setAlignment(Element.ALIGN_CENTER);
        rodape.setSpacingBefore(14);

        documento.add(rodape);
    }

    private void adicionarInformacao(
            PdfPTable tabela,
            String rotulo,
            String valor) {

        PdfPCell celula = new PdfPCell();
        celula.setPadding(10);
        celula.setBorderColor(new Color(220, 227, 236));

        Paragraph titulo = new Paragraph(
                rotulo.toUpperCase(),
                criarFonte(8, Font.NORMAL, Color.GRAY)
        );

        Paragraph conteudo = new Paragraph(
                valor,
                criarFonte(10, Font.BOLD, COR_TEXTO)
        );

        titulo.setSpacingAfter(5);

        celula.addElement(titulo);
        celula.addElement(conteudo);
        tabela.addCell(celula);
    }

    private void adicionarTotal(
            PdfPTable tabela,
            String rotulo,
            String valor,
            Color corValor) {

        PdfPCell celula = new PdfPCell();
        celula.setPadding(11);
        celula.setBackgroundColor(new Color(248, 250, 252));
        celula.setBorderColor(new Color(220, 227, 236));

        Paragraph titulo = new Paragraph(
                rotulo,
                criarFonte(9, Font.NORMAL, Color.GRAY)
        );

        Paragraph conteudo = new Paragraph(
                valor,
                criarFonte(14, Font.BOLD, corValor)
        );

        titulo.setSpacingAfter(6);

        celula.addElement(titulo);
        celula.addElement(conteudo);
        tabela.addCell(celula);
    }

    private void adicionarCabecalhoTabela(
            PdfPTable tabela,
            String texto) {

        PdfPCell celula = new PdfPCell(
                new Phrase(
                        texto.toUpperCase(),
                        criarFonte(
                                8,
                                Font.BOLD,
                                COR_TEXTO
                        )
                )
        );

        celula.setPadding(7);
        celula.setBackgroundColor(COR_CABECALHO);
        celula.setBorderColor(new Color(220, 227, 236));

        tabela.addCell(celula);
    }

    private void adicionarCelula(
            PdfPTable tabela,
            String texto,
            int alinhamento,
            Color cor) {

        PdfPCell celula = new PdfPCell(
                new Phrase(
                        texto,
                        criarFonte(8, Font.NORMAL, cor)
                )
        );

        celula.setPadding(7);
        celula.setVerticalAlignment(Element.ALIGN_MIDDLE);
        celula.setHorizontalAlignment(alinhamento);
        celula.setBorderColor(new Color(226, 232, 240));

        tabela.addCell(celula);
    }

    private Font criarFonte(
            float tamanho,
            int estilo,
            Color cor) {

        return FontFactory.getFont(
                FontFactory.HELVETICA,
                tamanho,
                estilo,
                cor
        );
    }

    private String formatarData(LocalDate data) {
        return data == null ? "-" : data.format(FORMATO_DATA);
    }

    private String formatarMoeda(BigDecimal valor) {
        return FORMATO_MOEDA.format(valorOuZero(valor));
    }

    private BigDecimal valorOuZero(BigDecimal valor) {
        return valor == null ? BigDecimal.ZERO : valor;
    }

    private String textoOuPadrao(
            String texto,
            String padrao) {

        return texto == null || texto.isBlank()
                ? padrao
                : texto;
    }
}