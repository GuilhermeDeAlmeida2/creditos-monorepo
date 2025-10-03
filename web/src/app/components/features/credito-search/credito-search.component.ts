import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService, Credito } from '../../../api.service';
import { CardComponent } from '../../ui/card/card.component';
import { ButtonComponent } from '../../ui/button/button.component';
import { InputComponent } from '../../ui/input/input.component';
import { BadgeComponent } from '../../ui/badge/badge.component';
import { ErrorMessageComponent } from '../../ui/error-message/error-message.component';
import { CurrencyFormatPipe } from '../../shared/pipes/currency-format.pipe';
import { DateFormatPipe } from '../../shared/pipes/date-format.pipe';
import { PercentageFormatPipe } from '../../shared/pipes/percentage-format.pipe';

@Component({
  selector: 'app-credito-search',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    CardComponent,
    ButtonComponent,
    InputComponent,
    BadgeComponent,
    ErrorMessageComponent,
    CurrencyFormatPipe,
    DateFormatPipe,
    PercentageFormatPipe,
  ],
  templateUrl: './credito-search.component.html',
  styleUrls: ['./credito-search.component.css'],
})
export class CreditoSearchComponent implements OnInit {
  numeroCredito: string = '';
  credito: Credito | null = null;
  loading: boolean = false;
  errorMessage: string = '';

  constructor(private apiService: ApiService) {}

  ngOnInit(): void {}

  buscarCredito(): void {
    if (!this.numeroCredito.trim()) {
      this.errorMessage = 'Por favor, digite um número de crédito válido.';
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.credito = null;

    this.apiService.buscarCreditoPorNumero(this.numeroCredito.trim()).subscribe({
      next: response => {
        this.credito = response;
        this.loading = false;
      },
      error: error => {
        if (error.status === 404) {
          this.errorMessage = `Nenhum crédito encontrado para o número: ${this.numeroCredito}`;
        } else {
          this.errorMessage = `Erro ao buscar crédito: ${error.message || 'Erro interno do servidor'}`;
        }
        this.loading = false;
      },
    });
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString('pt-BR');
  }

  formatCurrency(value: number): string {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL',
    }).format(value);
  }
}
