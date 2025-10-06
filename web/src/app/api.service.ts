import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../environments/environment';

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

export interface TestDataResponse {
  registrosGerados?: number;
  registrosDeletados?: number;
  mensagem: string;
  erro?: string;
}

@Injectable({
  providedIn: 'root',
})
export class ApiService {
  private readonly API_BASE_URL = environment.apiBaseUrl;

  constructor(private http: HttpClient) {}

  ping(): Observable<PingResponse> {
    return this.http.get<PingResponse>(`${this.API_BASE_URL}/api/ping`);
  }

  buscarCreditosPorNfse(
    numeroNfse: string,
    page: number = 0,
    size: number = 10,
    sortBy: string = 'dataConstituicao',
    sortDirection: 'asc' | 'desc' = 'desc'
  ): Observable<PaginatedCreditoResponse> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy)
      .set('sortDirection', sortDirection);

    return this.http.get<PaginatedCreditoResponse>(
      `${this.API_BASE_URL}/api/creditos/paginated/${numeroNfse}`,
      { params }
    );
  }

  buscarCreditoPorNumero(numeroCredito: string): Observable<Credito> {
    return this.http.get<Credito>(`${this.API_BASE_URL}/api/creditos/credito/${numeroCredito}`);
  }

  gerarRegistrosTeste(): Observable<TestDataResponse> {
    return this.http.post<TestDataResponse>(`${this.API_BASE_URL}/api/creditos/teste/gerar`, {});
  }

  deletarRegistrosTeste(): Observable<TestDataResponse> {
    return this.http.delete<TestDataResponse>(`${this.API_BASE_URL}/api/creditos/teste/deletar`);
  }
}
