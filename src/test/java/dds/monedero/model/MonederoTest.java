package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MonederoTest {
  private Cuenta cuenta;

  @BeforeEach
  void init() {
    cuenta = new Cuenta();
  }

  @Test
  void PonerMilQuinientosDeberíaTener1500() {
    cuenta.poner(1500);
    Assertions.assertEquals(1500, cuenta.getSaldo());
  }

  @Test
  void PonerMontoNegativoDeberíaArrojarExcepción() {
    Exception exception = assertThrows(RuntimeException.class, () -> {
      cuenta.poner(-1500);
    });

    String expectedMessage = -1500.0 + ": el monto a ingresar debe ser un valor positivo";
    String actualMessage = exception.getMessage();
    Assertions.assertEquals(expectedMessage, actualMessage);

  }

  @Test
  void TresDepositosDeberíaDarLaSumaDeLosMismos() {
    cuenta.poner(1500);
    cuenta.poner(456);
    cuenta.poner(1900);

    assertEquals(cuenta.getSaldo(), 3856);
  }

  @Test
  void MasDeTresDepositosDeberíaArrojarUnaExcepción() {
    Exception exception = assertThrows(RuntimeException.class, () -> {
          cuenta.poner(1500);
          cuenta.poner(456);
          cuenta.poner(1900);
          cuenta.poner(245);
    });

    String expectedMessage = "Ya excedio los " + 3 + " depositos diarios";
    String actualMessage = exception.getMessage();

    assertEquals(expectedMessage, actualMessage);
  }

  @Test
  void ExtraerMasQueElSaldoDeberíaArrojarUnaExcepción() {
    Exception exception = assertThrows(RuntimeException.class, () -> {
          cuenta.setSaldo(90);
          cuenta.sacar(1001);
    });

    String expectedMessage = "No se puede sacar mas de " + cuenta.getSaldo() + "$";
    String actualMessage = exception.getMessage();

    assertEquals(expectedMessage, actualMessage);
  }

  @Test
  public void ExtraerMasDe1000DeberíaArrojarUnaExcepción() {
    Exception exception = assertThrows(MaximoExtraccionDiarioException.class, () -> {
      cuenta.setSaldo(5000);
      cuenta.sacar(1001);
    });

    String expectedMessage = "No se puede extraer mas de $" + 1000 + "diarios, limite: " + cuenta.calcularLimiteActual();
    String actualMessage = exception.getMessage();

    assertEquals(expectedMessage, actualMessage);

  }

}