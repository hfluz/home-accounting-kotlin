package com.hfluz.accounting.controller

import com.hfluz.accounting.dao.TransacaoDAO
import com.hfluz.accounting.model.enumeration.TipoTransacao
import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDate
import javax.annotation.PostConstruct
import javax.enterprise.context.RequestScoped
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by hfluz on 04/05/17.
 */
@Named
@RequestScoped
class SituacaoMesController : Serializable {
    @Inject
    private lateinit var transacaoDAO: TransacaoDAO

    var porcentagemOrcamento: Int? = null
        private set

    @PostConstruct
    fun init() {
        val now = LocalDate.now()
        val transacoes = transacaoDAO.buscar(now.monthValue, now.year)
        val totalReceitas = transacoes.filter { TipoTransacao.RECEITA == it.tipoTransacao }
                .map { it.valor }
                .fold(BigDecimal.ZERO) { a, b -> a.add(b) }
        val totalDespesas = transacoes.filter { TipoTransacao.DESPESA == it.tipoTransacao }
                .map { it.valor }
                .fold(BigDecimal.ZERO) { a, b -> a.add(b) }
        if (totalReceitas == BigDecimal.ZERO) {
            porcentagemOrcamento = 0
        } else if (totalDespesas >= totalReceitas) {
            porcentagemOrcamento = 100
        } else {
            porcentagemOrcamento = (totalDespesas * BigDecimal(100) / totalReceitas).toInt()
        }
    }
}
