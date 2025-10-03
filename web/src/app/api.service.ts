import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface PingResponse {
  message: string;
  ts: string;
}

export interface Credito {
  id: number;
  numeroCredito: string;
  numeroNfse: string;
  dataConstituicao: string;
  valorIssqn: number;
  tipoCredito: string;
  simplesNacional: boolean;
  aliquota: number;
  valorFaturado: number;
  valorDeducao: number;
  baseCalculo: number;
}

export interface PaginatedCreditoResponse {
  content: Credito[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
  hasNext: boolean;
  hasPrevious: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  
  // Configuração da URL base da API
  // Opção 1: Via variável de ambiente (recomendado para produção)
  // private readonly API_BASE_URL = environment.apiBaseUrl;
  
  // Opção 2: Via arquivo assets/env.json (carregado em runtime)
  private readonly API_BASE_URL = 'http://localhost:8080';

  constructor(private http: HttpClient) { }

  ping(): Observable<PingResponse> {
    return this.http.get<PingResponse>(`${this.API_BASE_URL}/api/ping`);
  }

  buscarCreditosPorNfse(numeroNfse: string, page: number = 0, size: number = 10): Observable<PaginatedCreditoResponse> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    return this.http.get<PaginatedCreditoResponse>(`${this.API_BASE_URL}/api/creditos/paginated/${numeroNfse}`, { params });
  }
}
