import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface PingResponse {
  message: string;
  ts: string;
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
}
