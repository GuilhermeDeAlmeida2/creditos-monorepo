import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ApiService, PingResponse, Credito, PaginatedCreditoResponse } from './api.service';

describe('ApiService', () => {
  let service: ApiService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ApiService],
    });
    service = TestBed.inject(ApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('ping', () => {
    it('should return ping response', () => {
      const mockResponse: PingResponse = {
        message: 'pong',
        ts: '2025-10-03T00:58:09.731155Z',
      };

      service.ping().subscribe(response => {
        expect(response).toEqual(mockResponse);
      });

      const req = httpMock.expectOne('http://localhost:8080/api/ping');
      expect(req.request.method).toBe('GET');
      req.flush(mockResponse);
    });

    it('should handle ping error', () => {
      service.ping().subscribe({
        next: () => fail('should have failed'),
        error: error => {
          expect(error.status).toBe(500);
        },
      });

      const req = httpMock.expectOne('http://localhost:8080/api/ping');
      req.flush('Server Error', { status: 500, statusText: 'Internal Server Error' });
    });
  });

  describe('buscarCreditosPorNfse', () => {
    it('should return paginated creditos response', () => {
      const mockResponse: PaginatedCreditoResponse = {
        content: [
          {
            id: 1,
            numeroCredito: '123456',
            numeroNfse: '7891011',
            dataConstituicao: '2024-02-25',
            valorIssqn: 1500.75,
            tipoCredito: 'ISSQN',
            simplesNacional: true,
            aliquota: 5.0,
            valorFaturado: 30000.0,
            valorDeducao: 5000.0,
            baseCalculo: 25000.0,
          },
        ],
        page: 0,
        size: 10,
        totalElements: 1,
        totalPages: 1,
        first: true,
        last: true,
        hasNext: false,
        hasPrevious: false,
      };

      service.buscarCreditosPorNfse('7891011', 0, 10).subscribe(response => {
        expect(response).toEqual(mockResponse);
      });

      const req = httpMock.expectOne(
        'http://localhost:8080/api/creditos/paginated/7891011?page=0&size=10&sortBy=dataConstituicao&sortDirection=desc'
      );
      expect(req.request.method).toBe('GET');
      req.flush(mockResponse);
    });

    it('should use default page and size parameters', () => {
      const mockResponse: PaginatedCreditoResponse = {
        content: [],
        page: 0,
        size: 10,
        totalElements: 0,
        totalPages: 0,
        first: true,
        last: true,
        hasNext: false,
        hasPrevious: false,
      };

      service.buscarCreditosPorNfse('7891011').subscribe(response => {
        expect(response).toEqual(mockResponse);
      });

      const req = httpMock.expectOne(
        'http://localhost:8080/api/creditos/paginated/7891011?page=0&size=10&sortBy=dataConstituicao&sortDirection=desc'
      );
      expect(req.request.method).toBe('GET');
      req.flush(mockResponse);
    });

    it('should handle 404 error for non-existent NFS-e', () => {
      service.buscarCreditosPorNfse('9999999').subscribe({
        next: () => fail('should have failed'),
        error: error => {
          expect(error.status).toBe(404);
        },
      });

      const req = httpMock.expectOne(
        'http://localhost:8080/api/creditos/paginated/9999999?page=0&size=10&sortBy=dataConstituicao&sortDirection=desc'
      );
      req.flush('Not Found', { status: 404, statusText: 'Not Found' });
    });

    it('should handle server error', () => {
      service.buscarCreditosPorNfse('7891011').subscribe({
        next: () => fail('should have failed'),
        error: error => {
          expect(error.status).toBe(500);
        },
      });

      const req = httpMock.expectOne(
        'http://localhost:8080/api/creditos/paginated/7891011?page=0&size=10&sortBy=dataConstituicao&sortDirection=desc'
      );
      req.flush('Server Error', { status: 500, statusText: 'Internal Server Error' });
    });
  });

  describe('buscarCreditoPorNumero', () => {
    it('should return credito by numeroCredito', () => {
      const mockCredito: Credito = {
        id: 1,
        numeroCredito: '123456',
        numeroNfse: '7891011',
        dataConstituicao: '2024-02-25',
        valorIssqn: 1500.75,
        tipoCredito: 'ISSQN',
        simplesNacional: true,
        aliquota: 5.0,
        valorFaturado: 30000.0,
        valorDeducao: 5000.0,
        baseCalculo: 25000.0,
      };

      service.buscarCreditoPorNumero('123456').subscribe(response => {
        expect(response).toEqual(mockCredito);
      });

      const req = httpMock.expectOne('http://localhost:8080/api/creditos/credito/123456');
      expect(req.request.method).toBe('GET');
      req.flush(mockCredito);
    });

    it('should handle 404 error for non-existent credito', () => {
      service.buscarCreditoPorNumero('999999').subscribe({
        next: () => fail('should have failed'),
        error: error => {
          expect(error.status).toBe(404);
        },
      });

      const req = httpMock.expectOne('http://localhost:8080/api/creditos/credito/999999');
      req.flush('Not Found', { status: 404, statusText: 'Not Found' });
    });

    it('should handle server error', () => {
      service.buscarCreditoPorNumero('123456').subscribe({
        next: () => fail('should have failed'),
        error: error => {
          expect(error.status).toBe(500);
        },
      });

      const req = httpMock.expectOne('http://localhost:8080/api/creditos/credito/123456');
      req.flush('Server Error', { status: 500, statusText: 'Internal Server Error' });
    });

    it('should handle network error', () => {
      service.buscarCreditoPorNumero('123456').subscribe({
        next: () => fail('should have failed'),
        error: error => {
          expect(error.status).toBe(0);
        },
      });

      const req = httpMock.expectOne('http://localhost:8080/api/creditos/credito/123456');
      req.error(new ErrorEvent('Network error'));
    });
  });

  describe('gerarRegistrosTeste', () => {
    it('should generate test records', () => {
      const mockResponse = {
        registrosGerados: 100,
        mensagem: 'Registros de teste gerados com sucesso'
      };

      service.gerarRegistrosTeste().subscribe(response => {
        expect(response).toEqual(mockResponse);
      });

      const req = httpMock.expectOne('http://localhost:8080/api/creditos/teste/gerar');
      expect(req.request.method).toBe('POST');
      req.flush(mockResponse);
    });

    it('should handle error when generating test records', () => {
      service.gerarRegistrosTeste().subscribe({
        next: () => fail('should have failed'),
        error: error => {
          expect(error.status).toBe(500);
        },
      });

      const req = httpMock.expectOne('http://localhost:8080/api/creditos/teste/gerar');
      req.flush('Server Error', { status: 500, statusText: 'Internal Server Error' });
    });
  });

  describe('deletarRegistrosTeste', () => {
    it('should delete test records', () => {
      const mockResponse = {
        registrosDeletados: 100,
        mensagem: 'Registros de teste deletados com sucesso'
      };

      service.deletarRegistrosTeste().subscribe(response => {
        expect(response).toEqual(mockResponse);
      });

      const req = httpMock.expectOne('http://localhost:8080/api/creditos/teste/deletar');
      expect(req.request.method).toBe('DELETE');
      req.flush(mockResponse);
    });

    it('should handle error when deleting test records', () => {
      service.deletarRegistrosTeste().subscribe({
        next: () => fail('should have failed'),
        error: error => {
          expect(error.status).toBe(500);
        },
      });

      const req = httpMock.expectOne('http://localhost:8080/api/creditos/teste/deletar');
      req.flush('Server Error', { status: 500, statusText: 'Internal Server Error' });
    });
  });

  describe('buscarCreditosPorNfse with custom parameters', () => {
    it('should handle custom sort parameters', () => {
      const mockResponse: PaginatedCreditoResponse = {
        content: [],
        page: 0,
        size: 20,
        totalElements: 0,
        totalPages: 0,
        first: true,
        last: true,
        hasNext: false,
        hasPrevious: false,
      };

      service.buscarCreditosPorNfse('7891011', 0, 20, 'valorIssqn', 'asc').subscribe(response => {
        expect(response).toEqual(mockResponse);
      });

      const req = httpMock.expectOne(
        'http://localhost:8080/api/creditos/paginated/7891011?page=0&size=20&sortBy=valorIssqn&sortDirection=asc'
      );
      expect(req.request.method).toBe('GET');
      req.flush(mockResponse);
    });

    it('should handle different page numbers', () => {
      const mockResponse: PaginatedCreditoResponse = {
        content: [],
        page: 2,
        size: 10,
        totalElements: 0,
        totalPages: 0,
        first: false,
        last: true,
        hasNext: false,
        hasPrevious: true,
      };

      service.buscarCreditosPorNfse('7891011', 2, 10).subscribe(response => {
        expect(response).toEqual(mockResponse);
      });

      const req = httpMock.expectOne(
        'http://localhost:8080/api/creditos/paginated/7891011?page=2&size=10&sortBy=dataConstituicao&sortDirection=desc'
      );
      expect(req.request.method).toBe('GET');
      req.flush(mockResponse);
    });
  });

  describe('API Base URL', () => {
    it('should use correct API base URL', () => {
      // Teste para verificar se a URL base está sendo usada corretamente
      service.ping().subscribe();

      const req = httpMock.expectOne('http://localhost:8080/api/ping');
      expect(req.request.url).toBe('http://localhost:8080/api/ping');
      req.flush({ message: 'pong', ts: '2025-10-05T19:00:00Z' });
    });
  });
});