package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Cuenta {

  private double saldo = 0;
  private List<Movimiento> movimientos = new ArrayList<>();

  //Acá hay code smell al momento de crear la cuenta.
  public Cuenta(double montoInicial){
    this.saldo = montoInicial;
  }

  public Cuenta(){
    this(0);
  }

  public void setMovimientos(List<Movimiento> movimientos){
    this.movimientos = movimientos;
  }
  public void poner(double cuanto) {
    //se podria pasar a otro método
    validarMontoNegativo(cuanto);
    if(cantidadDeDepositosDiarios() >= 3)
      throw new MaximaCantidadDepositosException("Ya excedio los " + 3 + " depositos diarios");

    new Deposito(LocalDate.now(), cuanto).agregateA(this);
  }

  public void sacar(double cuanto) {
    //Se podría pasar a otro método
    validarMontoNegativo(cuanto);
    validarExtraccion(cuanto);

    new Extraccion(LocalDate.now(), cuanto).agregateA(this);

  }

  public void validarExtraccion(double cuanto){
    if(getSaldo() - cuanto < 0)
      throw new SaldoMenorException("No se puede sacar mas de " + getSaldo() + "$");

    if(cuanto > this.calcularLimiteActual())
      throw new MaximoExtraccionDiarioException("No se puede extraer mas de $" + 1000 + "diarios, limite: " + calcularLimiteActual());
  }

  public void validarMontoNegativo(double cuanto) {
    if (cuanto <= 0)
      throw new MontoNegativoException(cuanto + ": el monto a ingresar debe ser un valor positivo");
  }

  public long cantidadDeDepositosDiarios(){
    return getMovimientos().stream()
          .filter(movimiento -> movimiento.isDeposito() && movimiento.getFecha().equals(LocalDate.now()))
          .count();
  }

  //
  public void agregarMovimiento(Movimiento movimiento) {
    movimientos.add(movimiento);
  }

  public double calcularLimiteActual ()
  {
    return 1000 - this.getMontoExtraidoA(LocalDate.now());
  }

  public double getMontoExtraidoA(LocalDate fecha) {
    return getMovimientos().stream()
        .filter(movimiento -> !movimiento.isDeposito() && movimiento.getFecha().equals(fecha))
        .mapToDouble(Movimiento::getMonto)
        .sum();
  }

  public List<Movimiento> getMovimientos() {
    return movimientos;
  }
  public double getSaldo() {
    return saldo;
  }
  public void setSaldo(double saldo) {
    this.saldo = saldo;
  }

}
