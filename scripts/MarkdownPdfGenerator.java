import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

public class MarkdownPdfGenerator {
    private static final float MARGIN = 54;
    private static final float FOOTER_Y = 28;
    private static final PDRectangle PAGE_SIZE = PDRectangle.A4;

    private final PDFont regular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
    private final PDFont bold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
    private final PDDocument document = new PDDocument();
    private PDPage page;
    private PDPageContentStream content;
    private float y;
    private int pageNumber = 0;

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            throw new IllegalArgumentException("Uso: MarkdownPdfGenerator entrada.md saida.pdf");
        }

        Path input = Path.of(args[0]);
        Path output = Path.of(args[1]);
        new MarkdownPdfGenerator().generate(input, output);
    }

    private void generate(Path input, Path output) throws IOException {
        Files.createDirectories(output.getParent());
        addPage();

        List<String> lines = Files.readAllLines(input, StandardCharsets.UTF_8);
        StringBuilder paragraph = new StringBuilder();

        for (String rawLine : lines) {
            String line = rawLine.strip();
            if (line.isBlank()) {
                flushParagraph(paragraph);
                continue;
            }

            if (line.startsWith("#")) {
                flushParagraph(paragraph);
                writeHeading(line);
            } else if (line.startsWith("- ")) {
                flushParagraph(paragraph);
                writeBullet(line.substring(2));
            } else if (line.matches("\\d+\\. .*")) {
                flushParagraph(paragraph);
                writeParagraph(line, regular, 10.8f, 15f, 10f);
            } else {
                if (!paragraph.isEmpty()) {
                    paragraph.append(' ');
                }
                paragraph.append(line);
            }
        }

        flushParagraph(paragraph);
        closePage();
        document.save(output.toFile());
        document.close();
    }

    private void flushParagraph(StringBuilder paragraph) throws IOException {
        if (paragraph.isEmpty()) {
            return;
        }
        writeParagraph(paragraph.toString(), regular, 10.8f, 15f, 8f);
        paragraph.setLength(0);
    }

    private void writeHeading(String line) throws IOException {
        int level = 0;
        while (level < line.length() && line.charAt(level) == '#') {
            level++;
        }

        String text = line.substring(level).strip();
        if (level == 1) {
            writeParagraph(text, bold, 17f, 21f, 12f);
        } else {
            writeParagraph(text, bold, 13.5f, 18f, 9f);
        }
    }

    private void writeBullet(String text) throws IOException {
        List<String> wrapped = wrap("- " + text, regular, 10.8f, contentWidth());
        ensureSpace(wrapped.size() * 15f + 3f);
        for (String line : wrapped) {
            showLine(line, regular, 10.8f, MARGIN + (line.startsWith("- ") ? 0 : 12), y);
            y -= 15f;
        }
        y -= 2f;
    }

    private void writeParagraph(String text, PDFont font, float fontSize, float lineHeight, float after) throws IOException {
        List<String> wrapped = wrap(text, font, fontSize, contentWidth());
        ensureSpace(wrapped.size() * lineHeight + after);
        for (String line : wrapped) {
            showLine(line, font, fontSize, MARGIN, y);
            y -= lineHeight;
        }
        y -= after;
    }

    private List<String> wrap(String text, PDFont font, float fontSize, float maxWidth) throws IOException {
        String normalized = normalize(text);
        String[] words = normalized.split("\\s+");
        List<String> lines = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        for (String word : words) {
            String candidate = current.isEmpty() ? word : current + " " + word;
            if (textWidth(candidate, font, fontSize) <= maxWidth) {
                current.setLength(0);
                current.append(candidate);
            } else {
                if (!current.isEmpty()) {
                    lines.add(current.toString());
                    current.setLength(0);
                }
                if (textWidth(word, font, fontSize) <= maxWidth) {
                    current.append(word);
                } else {
                    lines.add(word);
                }
            }
        }

        if (!current.isEmpty()) {
            lines.add(current.toString());
        }

        return lines;
    }

    private String normalize(String text) {
        return text
                .replace("->", "->")
                .replace("–", "-")
                .replace("—", "-")
                .replace("“", "\"")
                .replace("”", "\"")
                .replace("’", "'")
                .replace("`", "");
    }

    private float textWidth(String text, PDFont font, float fontSize) throws IOException {
        return font.getStringWidth(text) / 1000f * fontSize;
    }

    private float contentWidth() {
        return PAGE_SIZE.getWidth() - MARGIN * 2;
    }

    private void ensureSpace(float needed) throws IOException {
        if (y - needed < 56) {
            closePage();
            addPage();
        }
    }

    private void addPage() throws IOException {
        page = new PDPage(PAGE_SIZE);
        document.addPage(page);
        content = new PDPageContentStream(document, page);
        y = PAGE_SIZE.getHeight() - MARGIN;
        pageNumber++;
    }

    private void closePage() throws IOException {
        if (content == null) {
            return;
        }
        showLine("GenRoute GA - Seminário de Matemática Computacional | Página " + pageNumber,
                regular,
                8.5f,
                MARGIN,
                FOOTER_Y);
        content.close();
        content = null;
    }

    private void showLine(String text, PDFont font, float fontSize, float x, float lineY) throws IOException {
        content.beginText();
        content.setFont(font, fontSize);
        content.newLineAtOffset(x, lineY);
        content.showText(normalize(text));
        content.endText();
    }
}
