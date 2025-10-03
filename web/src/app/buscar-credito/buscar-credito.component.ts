import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService, Credito } from '../api.service';
import { CardComponent } from '../components/ui/card/card.component';
import { ButtonComponent } from '../components/ui/button/button.component';
import { InputComponent } from '../components/ui/input/input.component';
import { BadgeComponent } from '../components/ui/badge/badge.component';
import { ErrorMessageComponent } from '../components/ui/error-message/error-message.component';

@Component({
  selector: 'app-buscar-credito',
  standalone: true,
  imports: [CommonModule, FormsModule, CardComponent, ButtonComponent, InputComponent, BadgeComponent, ErrorMessageComponent],
  templateUrl: './buscar-credito.component.html',
  styleUrls: ['./buscar-credito.component.css']
})
export class BuscarCreditoComponent implements OnInit {
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
      next: (response) => {
        this.credito = response;
        this.loading = false;
      },
      error: (error) => {
        if (error.status === 404) {
          this.errorMessage = `Nenhum crédito encontrado para o número: ${this.numeroCredito}`;
        } else {
          this.errorMessage = `Erro ao buscar crédito: ${error.message || 'Erro interno do servidor'}`;
        }
        this.loading = false;
      }
    });
  }

  abrirEmNovaAba(): void {
    if (!this.numeroCredito.trim()) {
      this.errorMessage = 'Por favor, digite um número de crédito válido.';
      return;
    }

    // Criar URL para nova aba
    const url = this.gerarUrlNovaAba();
    window.open(url, '_blank');
  }

  private gerarUrlNovaAba(): string {
    // Obter a URL base atual
    const baseUrl = window.location.origin + window.location.pathname;
    
    // Criar parâmetros de busca
    const params = new URLSearchParams();
    params.set('numeroCredito', this.numeroCredito.trim());
    params.set('novaAba', 'true');
    
    return `${baseUrl}?${params.toString()}`;
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString('pt-BR');
  }

  formatCurrency(value: number): string {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    }).format(value);
  }
}
